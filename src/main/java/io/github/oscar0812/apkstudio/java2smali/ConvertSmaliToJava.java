package io.github.oscar0812.apkstudio.java2smali;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.oscar0812.JDSX.converters.FileUtils;
import io.github.oscar0812.apkstudio.common.OpenFileAction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConvertSmaliToJava extends AnAction implements DumbAware {

    @NotNull
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        // Since the update logic is lightweight, run it on the EDT.
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent e) {
        // Get the current file being edited
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile != null) {
            String fileExtension = virtualFile.getExtension();

            // Disable actions based on file type
            if (!"smali".equalsIgnoreCase(fileExtension)) {
                e.getPresentation().setEnabledAndVisible(false);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || !file.getName().endsWith(".smali")) {
            return;
        }

        // Access the document
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            String content = document.getText();
            try {
                Path javaOutputPath = io.github.oscar0812.JDSX.converters.Smali.convertSmaliToJava(content);
                List<Path> javaFiles = FileUtils.findFilesByExtension(javaOutputPath, ".java");
                javaFiles.forEach(p -> OpenFileAction.openFile(project, p.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
