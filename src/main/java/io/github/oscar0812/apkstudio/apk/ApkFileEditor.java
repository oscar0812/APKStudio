package io.github.oscar0812.apkstudio.apk;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Collectors;

public class ApkFileEditor implements FileEditor {
    private final JPanel panel;

    public ApkFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        panel = new JPanel();
        JLabel label = new JLabel("Decompiling APK: " + file.getPath());
        panel.add(label);

        // Perform the APK decompilation
        try {
            File decompiledDir = decompileApk(file.getPath());
            System.out.println("Decompiled to: " + decompiledDir.getAbsolutePath());
            label.setText("Decompiled to: " + decompiledDir.getAbsolutePath());
//            Project newProject = null;
//            try {
//                newProject = ProjectManager.getInstance().loadAndOpenProject(decompiledDir.getAbsolutePath());
//                if (newProject == null) {
//                    Messages.showErrorDialog("Failed to open the decompiled project", "Error");
//                }
//            } catch (Exception e) {
//                Messages.showErrorDialog("Error while opening project: " + e.getMessage(), "Error");
//            }
        } catch (Exception e) {
            label.setText("Failed to decompile APK: " + e.getMessage());
        }

        // super(project, file)
    }

    public File decompileApk(String apkPath) throws Exception {
        String randomChars = System.currentTimeMillis() + new Random().ints(8, 'a', 'z' + 1)
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
        String outputDir = apkPath.replace(".apk", "_decompiled_" + randomChars);

        // Decompile the APK
        try {
            // Decompile APK using the provided Apktool API
            String[] newArgs = { "d", apkPath, "-o", outputDir, "-f" };
            brut.apktool.Main.main(newArgs);
        } catch (Exception e) {
            // Handle exceptions, such as Apktool errors
            throw new Exception("Failed to decompile APK: " + e.getMessage(), e);
        }

        return new File(outputDir);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return panel;
    }

    @Override
    public @NotNull String getName() {
        return "APK Viewer";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void dispose() {
        // Cleanup resources
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }
}

