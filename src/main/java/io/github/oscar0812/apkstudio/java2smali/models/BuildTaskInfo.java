package io.github.oscar0812.apkstudio.java2smali.models;

import java.nio.file.Path;
import java.util.Objects;

public class BuildTaskInfo {
    private BuildTaskType buildTaskType;
    private Path buildPath;
    private Path classesPath;

    public BuildTaskInfo(BuildTaskType buildTaskType, Path buildPath) {
        this.buildTaskType = buildTaskType;
        this.buildPath = buildPath;
        this.classesPath = this.buildPath.resolve("out");

        if(buildTaskType == BuildTaskType.MAVEN) {
            // target/ -> target/classes/
            this.classesPath = this.buildPath.resolve("target")
                    .resolve("classes");
        } else if(buildTaskType == BuildTaskType.GRADLE) {
            // build/ -> build/classes/java/main/
            this.classesPath = this.buildPath.resolve("build")
                    .resolve("classes").resolve("java").resolve("main");
        }
    }

    public Path getBuildPath() {
        return this.buildPath;
    }

    public Path getClassesPath() {
        return classesPath;
    }

    public boolean success() {
        return !Objects.isNull(buildPath);
    }
}
