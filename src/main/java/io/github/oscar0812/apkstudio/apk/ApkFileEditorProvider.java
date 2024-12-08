package io.github.oscar0812.apkstudio.apk;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class ApkFileEditorProvider implements FileEditorProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        // Check if the file is an APK
        return file.getName().endsWith(".apk");
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "APKFileEditor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.NONE;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        // Handle APK file opening (e.g., decompile and show the folder)
        return new ApkFileEditor(project, file);
    }

    @Override
    public void disposeEditor(@NotNull FileEditor editor) {
        // Cleanup resources if necessary
    }

    @Override
    public @NotNull FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement) {
        // Save editor state if necessary
    }
}

