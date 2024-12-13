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
import io.github.oscar0812.apkstudio.common.BuildInvoker;
import io.github.oscar0812.apkstudio.common.OpenFileAction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConvertJavaToSmali extends AnAction implements DumbAware {

    @NotNull
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        // Since the update logic is lightweight, run it on the EDT.
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent e) {
        // Get the current file being edited
        VirtualFile file = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE);
        if (file != null) {
            String fileExtension = file.getExtension();

            // Disable actions based on file type
            if (!"java".equalsIgnoreCase(fileExtension)) {
                e.getPresentation().setEnabledAndVisible(false);
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || !file.getName().endsWith(".java")) {
            return;
        }

        // Access the document
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {

            BuildInvoker.buildProject(project, (buildTaskInfo) -> {
                if (buildTaskInfo.success()) {
                    System.out.println("Build path: " + buildTaskInfo.getBuildPath());
                    System.out.println("Classes path: " + buildTaskInfo.getClassesPath());
                    postBuild(buildTaskInfo.getClassesPath(), project, file, document);
                } else {
                    System.out.println("Build failed.");
                }
            });
        }
    }

    public void postBuild(Path buildPath, Project project, VirtualFile file, Document document) {
        String content = document.getText();
        Path packageSubPath = PackageExtractor.getPathFromPackage(content);
        Path fullPath = buildPath.resolve(packageSubPath);
        System.out.println("FULL: " + fullPath);
        try {
            List<Path> classes = FileUtils.findClassFiles(fullPath, file.getNameWithoutExtension());
            if (!classes.isEmpty()) {
                Path smaliOutputPath = io.github.oscar0812.JDSX.converters.Class.convertClassFilesToSmali(classes.get(0));
                System.out.println("SMALI: " + smaliOutputPath);
                List<Path> smaliFiles = FileUtils.findSmaliFiles(smaliOutputPath, file.getNameWithoutExtension());
                smaliFiles.forEach(p -> OpenFileAction.openFile(project, p.toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
