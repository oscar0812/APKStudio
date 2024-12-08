package io.github.oscar0812.apkstudio.apk;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.io.IOException;

public class OpenApkAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("No open project found", "Error");
            return;
        }

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter(file -> file.getName().endsWith(".apk"))
                .withTitle("Select APK File");
        FileChooser.chooseFile(descriptor, project, null, virtualFile -> {
            String apkPath = virtualFile.getPath();
            try {
                File decompiledDir = decompileApk(apkPath);
                openInProject(project, decompiledDir);
            } catch (Exception ex) {
                Messages.showErrorDialog("Failed to decompile APK: " + ex.getMessage(), "Error");
            }
        });
    }

    private File decompileApk(String apkPath) throws IOException, InterruptedException {
        String outputDir = apkPath.replace(".apk", "_decompiled");
        Process process = new ProcessBuilder("apktool", "d", apkPath, "-o", outputDir)
                .redirectErrorStream(true)
                .start();
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new IOException("apktool failed to decompile APK");
        }
        return new File(outputDir);
    }

    private void openInProject(Project project, File decompiledDir) {
        com.intellij.openapi.vfs.VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
        Messages.showInfoMessage("Decompiled folder located at: " + decompiledDir.getAbsolutePath(), "Decompilation Complete");
    }
}

