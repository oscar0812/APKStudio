package io.github.oscar0812.apkstudio.apk;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class ApkFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(ApkFileType.INSTANCE, ApkFileType.INSTANCE.getDefaultExtension());
    }
}

