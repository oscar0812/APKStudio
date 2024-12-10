package io.github.oscar0812.apkstudio.apk;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.oscar0812.apkstudio.common.ReindexAction;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BuildAPKAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null) {
            String fileName = file.getName();

            if (!"apktool.yml".equalsIgnoreCase(fileName)) {
                e.getPresentation().setEnabledAndVisible(false);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null) {
            return;
        }

        Path apkPath = Path.of(file.getPath());
        CountDownLatch latch = new CountDownLatch(1);
        runBuild(project, apkPath, latch, (output) -> {
            System.out.println("Build complete, output: " + output);
        });
    }

    public static void runBuild(Project project, Path apkYamlPath, CountDownLatch latch, Consumer<Path> callback) {
        ProgressManager.getInstance().run(new com.intellij.openapi.progress.Task.Modal(project, "Building APK", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                try {

                    indicator.setText("Building APK...");
                    buildApkWithProgress(apkYamlPath, indicator);

                    indicator.setText("Refreshing and indexing dist...");
                    Path distPath = apkYamlPath.getParent().resolve("dist");
                    ReindexAction.reindexDirectory(apkYamlPath.getParent().resolve("dist"));

                    indicator.stop();

                    if(Objects.nonNull(callback)) {
                        callback.accept(distPath);
                    }

                    if(Objects.nonNull(latch)) {
                        latch.countDown();
                    }

                } catch (Exception ex) {
                    if (indicator.isCanceled()) {
                        Messages.showInfoMessage(project, "Build was canceled.", "Build Canceled");
                    } else {
                        Messages.showErrorDialog(project, "Failed to build APK: " + ex.getMessage(), "Error");
                    }
                    if(Objects.nonNull(latch)) {
                        latch.countDown();
                    }
                }
            }
        });
    }

    private static void buildApkWithProgress(Path apkYamlPath, ProgressIndicator indicator) throws Exception {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try {
            // Create a custom OutputStream to capture Apktool logs
            OutputStream logOutputStream = new OutputStream() {
                private final StringBuilder buffer = new StringBuilder();

                @Override
                public void write(int b) {
                    // Append the character to the buffer
                    buffer.append((char) b);

                    // When a newline is detected, send the log to the progress indicator
                    if (b == '\n') {
                        if(!Objects.isNull(indicator)) {
                            indicator.setText("Log: " + buffer.toString().trim());
                            buffer.setLength(0); // Clear the buffer
                        }
                    }
                }
            };

            // Redirect System.out and System.err
            System.setOut(new PrintStream(logOutputStream));
            System.setErr(new PrintStream(logOutputStream));

            indicator.setText("Starting building...");
            indicator.setIndeterminate(true);

            String[] newArgs = {"b", apkYamlPath.getParent().toString()};
            brut.apktool.Main.main(newArgs);

        } catch (Exception e) {
            e.printStackTrace();
            if (indicator.isCanceled()) {
                throw new InterruptedException("Build was canceled by the user.");
            } else {
                throw new Exception("Failed to build APK: " + e.getMessage(), e);
            }
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
