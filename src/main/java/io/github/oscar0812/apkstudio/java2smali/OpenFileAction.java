package io.github.oscar0812.apkstudio.java2smali;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

public class OpenFileAction {

    public static void openFile(Project project, String filePath) {
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);

        if (file != null) {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(file, true);
        } else {
            System.out.println("File not found: " + filePath);
        }
    }
}
