package io.github.oscar0812.apkstudio.apk;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.stream.Collectors;

public class DecompileAPKAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null) {
            String fileExtension = file.getExtension();

            if (!"apk".equalsIgnoreCase(fileExtension)) {
                e.getPresentation().setEnabled(false);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No open project found", "Error");
            return;
        }

        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || !file.getName().endsWith(".apk")) {
            return;
        }

        String apkPath = file.getPath();

        ProgressManager.getInstance().run(new com.intellij.openapi.progress.Task.Modal(project, "Decompiling APK", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                try {
                    // Step 1: Create output directory
                    indicator.setText("Creating output directory...");
                    File outputDir = createOutputDirectory(apkPath);

                    // Step 2: Perform APK decompilation with progress updates
                    indicator.setText("Decompiling APK...");
                    decompileApkWithProgress(apkPath, outputDir.getAbsolutePath(), indicator);

                    // Step 3: Refresh and index the project
                    indicator.setText("Refreshing and indexing project...");
                    refreshAndIndexDirectoryWithIndicator(project, outputDir, indicator);

                    indicator.stop();
                } catch (Exception ex) {
                    if (indicator.isCanceled()) {
                        Messages.showInfoMessage(project, "Decompilation was canceled.", "Decompile Canceled");
                    } else {
                        Messages.showErrorDialog(project, "Failed to decompile APK: " + ex.getMessage(), "Error");
                    }
                }
            }
        });
    }

    private File createOutputDirectory(String apkPath) throws IOException {
        String randomChars = System.currentTimeMillis() + new Random().ints(8, 'a', 'z' + 1)
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
        String outputDirPath = apkPath.replace(".apk", "_decompiled_" + randomChars);

        File outputDir = new File(outputDirPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory: " + outputDirPath);
        }

        return outputDir;
    }

    private void refreshAndIndexDirectoryWithIndicator(Project project, File outputDir, ProgressIndicator indicator) {
        VirtualFile virtualOutputDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputDir);
        if (virtualOutputDir != null) {
            // Refresh the folder
            virtualOutputDir.refresh(true, false);

            FileBasedIndex.getInstance().requestReindex(virtualOutputDir);
        }
    }

    private void decompileApkWithProgress(String apkPath, String outputDirPath, ProgressIndicator indicator) throws Exception {
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
                        indicator.setText("Log: " + buffer.toString().trim());
                        buffer.setLength(0); // Clear the buffer
                    }
                }
            };

            // Redirect System.out and System.err
            System.setOut(new PrintStream(logOutputStream));
            System.setErr(new PrintStream(logOutputStream));

            // Run Apktool decompilation
            indicator.setText("Starting decompilation...");
            indicator.setIndeterminate(true);
            String[] newArgs = {"d", apkPath, "-o", outputDirPath, "-f"};
            brut.apktool.Main.main(newArgs);

        } catch (Exception e) {
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
