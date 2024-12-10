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

public class DecompileAPKAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null) {
            String fileExtension = file.getExtension();

            if (!"apk".equalsIgnoreCase(fileExtension)) {
                e.getPresentation().setEnabledAndVisible(false);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || !file.getName().endsWith(".apk")) {
            return;
        }

        Path apkPath = Path.of(file.getPath());
        CountDownLatch latch = new CountDownLatch(1);
        runDecompile(project, apkPath, latch, (output) -> {
            System.out.println("Decompilation complete, output: " + output);
        });
    }

    public static void runDecompile(Project project, Path apkPath, CountDownLatch latch, Consumer<Path> callback) {
        ProgressManager.getInstance().run(new com.intellij.openapi.progress.Task.Modal(project, "Decompiling APK", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                try {
                    indicator.setText("Creating output directory...");
                    Path outputDir = createOutputDirectory(apkPath);

                    indicator.setText("Decompiling APK...");
                    decompileApkWithProgress(apkPath, outputDir, indicator);

                    indicator.setText("Refreshing and indexing project...");
                    ReindexAction.reindexDirectory(outputDir);

                    indicator.stop();

                    if(Objects.nonNull(callback)) {
                        callback.accept(outputDir);
                    }

                    if(Objects.nonNull(latch)) {
                        latch.countDown();
                    }

                } catch (Exception ex) {
                    if (indicator.isCanceled()) {
                        Messages.showInfoMessage(project, "Decompilation was canceled.", "Decompile Canceled");
                    } else {
                        Messages.showErrorDialog(project, "Failed to decompile APK: " + ex.getMessage(), "Error");
                    }
                    if(Objects.nonNull(latch)) {
                        latch.countDown();
                    }
                }
            }
        });
    }

    private static Path createOutputDirectory(Path apkPath) throws IOException {
        String randomChars = System.currentTimeMillis() + new Random().ints(8, 'a', 'z' + 1)
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());

        Path outputDirPath = apkPath.resolveSibling(
                apkPath.getFileName().toString().replace(".apk", "_decompiled_" + randomChars));

        // Create the directory if it doesn't exist
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }

        return outputDirPath;
    }


    private static void decompileApkWithProgress(Path apkPath, Path outputDirPath, ProgressIndicator indicator) throws Exception {
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

            // Run Apktool decompilation
            if(!Objects.isNull(indicator)) {
                indicator.setText("Starting decompilation...");
                indicator.setIndeterminate(true);
            }
            String[] newArgs = {"d", apkPath.toString(), "-o", outputDirPath.toString(), "-f"};
            brut.apktool.Main.main(newArgs);

        } catch (Exception e) {
            e.printStackTrace();
            if (indicator.isCanceled()) {
                throw new InterruptedException("Decompilation was canceled by the user.");
            } else {
                throw new Exception("Failed to decompile APK: " + e.getMessage(), e);
            }
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
