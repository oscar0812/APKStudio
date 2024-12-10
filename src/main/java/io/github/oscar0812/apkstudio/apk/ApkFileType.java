package io.github.oscar0812.apkstudio.apk;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ApkFileType extends LanguageFileType {

    public static final ApkFileType INSTANCE = new ApkFileType();

    private ApkFileType() {
        // Use PlainTextLanguage as the associated language
        super(com.intellij.openapi.fileTypes.PlainTextLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "APK File";
    }

    @Override
    public @NotNull String getDescription() {
        return "Android APK file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "apk";
    }

    @Override
    public @Nullable Icon getIcon() {
        // Optionally provide an icon for APK files
        return AllIcons.FileTypes.Archive;
    }
}
