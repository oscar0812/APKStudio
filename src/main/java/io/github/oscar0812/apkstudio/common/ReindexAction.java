package io.github.oscar0812.apkstudio.common;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.FileBasedIndex;

import java.nio.file.Path;

public class ReindexAction {
    public static void reindexDirectory(Path directory) {
        VirtualFile virtualOutputDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(directory.toFile());
        if (virtualOutputDir != null) {
            // Refresh the folder
            virtualOutputDir.refresh(true, true);

            FileBasedIndex.getInstance().requestReindex(virtualOutputDir);
        }
    }
}
