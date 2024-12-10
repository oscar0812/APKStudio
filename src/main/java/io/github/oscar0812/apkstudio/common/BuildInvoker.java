package io.github.oscar0812.apkstudio.common;

import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.task.ProjectTask;
import com.intellij.task.ProjectTaskManager;
import io.github.oscar0812.apkstudio.java2smali.models.BuildTaskInfo;
import io.github.oscar0812.apkstudio.java2smali.models.BuildTaskType;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class BuildInvoker {

    public static void buildProject(Project project, Consumer<BuildTaskInfo> callback) {
        ProjectTaskManager taskManager = ProjectTaskManager.getInstance(project);

        ProjectTask buildTask = taskManager.createAllModulesBuildTask(true, project);
        taskManager.run(buildTask).onSuccess(result -> {
            Path buildPath = Path.of(Objects.requireNonNull(project.getBasePath()));

            com.intellij.openapi.module.Module[] modules = ModuleManager.getInstance(project).getModules();

            BuildTaskType type = Arrays.stream(modules)
                    .flatMap(module -> Arrays.stream(BuildTaskType.values())
                            .filter(taskType -> ExternalSystemApiUtil.isExternalSystemAwareModule(new ProjectSystemId(taskType.getDescription()), module))
                            .findFirst().stream())
                    .findFirst()
                    .orElse(BuildTaskType.OTHER);

            // If neither Maven nor Gradle, check for IntelliJ's default build system
            if(type == BuildTaskType.OTHER) {
                CompilerProjectExtension compilerProjectExtension = CompilerProjectExtension.getInstance(project);
                if (!Objects.isNull(compilerProjectExtension)) {
                    String outputPath = compilerProjectExtension.getCompilerOutputUrl();
                    if (!Objects.isNull(outputPath)) {
                        // Convert URL to a file path
                        buildPath = Path.of(outputPath.replace("file://", ""));
                        type = BuildTaskType.IDE;
                    }
                }
            }

            callback.accept(new BuildTaskInfo(type, buildPath));

        }).onError(result -> {
            callback.accept(new BuildTaskInfo(BuildTaskType.OTHER, null));
        });
    }
}
