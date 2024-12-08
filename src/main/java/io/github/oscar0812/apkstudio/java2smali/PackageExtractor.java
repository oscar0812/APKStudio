package io.github.oscar0812.apkstudio.java2smali;


import java.nio.file.Path;
import java.util.Optional;

public class PackageExtractor {

    public static Optional<String> getPackageFromContent(String content) {
        int packageIndex = content.indexOf("package ");

        if (packageIndex != -1) {
            String packageLine = content.substring(packageIndex);
            String packageName = packageLine.substring(8, packageLine.indexOf(";")).trim();
            return Optional.of(packageName);
        }

        return Optional.empty();
    }

    public static Path getPathFromPackage(String content) {
        Optional<String> pack = getPackageFromContent(content);
        String path = "";
        if(pack.isPresent()) {
            path = pack.get().replace('.', '/');
        }
        return Path.of(path);
    }
}
