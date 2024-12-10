package io.github.oscar0812.apkstudio.apk;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectOpenProcessor;
import io.github.oscar0812.apkstudio.common.OpenFileAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public class ApkProjectOpenProcessor extends ProjectOpenProcessor {

    @Override
    public boolean canOpenProject(@NotNull VirtualFile file) {
        return file.getFileType() instanceof ApkFileType;
    }

    @Override
    public @Nullable Project doOpenProject(@NotNull VirtualFile file, @Nullable Project existingProject, boolean forceOpenInNewFrame) {
        ProjectManager projectManager = ProjectManager.getInstance();
        Path apkPath = Path.of(file.getPath());

        CountDownLatch latch = new CountDownLatch(1);
        final Project[] openedProject = new Project[1]; // Array to store the opened project

        DecompileAPKAction.runDecompile(projectManager.getDefaultProject(), apkPath, latch, (outputDir) -> {
            System.out.println("Decompiled APK at: " + outputDir);

            VirtualFile outputVf = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputDir.toString());
            if (outputVf != null) {
                openedProject[0] = OpenFileAction.openDirectoryAsProjectFiles(outputVf);
            }
        });

        try {
            // Wait for the decompilation to finish
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return openedProject[0];
    }

    @Override
    public @NotNull String getName() {
        return "";
    }
}

