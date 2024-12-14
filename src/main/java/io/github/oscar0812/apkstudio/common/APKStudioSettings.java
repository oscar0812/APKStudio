package io.github.oscar0812.apkstudio.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.NotNull;

@Service
@State(name = "APKStudioSettings", storages = @Storage("apkstudioSettings.xml"))
public final class APKStudioSettings implements PersistentStateComponent<APKStudioSettings.State> {

    public static class State {
        public boolean forceBuild = false;
        public boolean forceDecode = false;

        public String buildOutputDir = "dist";
        public String decodeOutputDir = "decoded";

        public boolean decodeNoResources = false;
        public boolean decodeNoSources = false;
    }

    private static APKStudioSettings instance;

    public static APKStudioSettings getInstance() {
        if (instance == null) {
            instance = ApplicationManager.getApplication().getService(APKStudioSettings.class);
        }
        return instance;
    }

    private State state = new State();

    public boolean isForceBuild() {
        return state.forceBuild;
    }

    public void setForceBuild(boolean forceBuild) {
        state.forceBuild = forceBuild;
    }

    public boolean isForceDecode() {
        return state.forceDecode;
    }

    public void setForceDecode(boolean forceDecode) {
        state.forceDecode = forceDecode;
    }

    public String getBuildOutputDir() {
        return state.buildOutputDir;
    }

    public void setBuildOutputDir(String buildOutputDir) {
        state.buildOutputDir = buildOutputDir;
    }

    public String getDecodeOutputDir() {
        return state.decodeOutputDir;
    }

    public void setDecodeOutputDir(String decodeOutputDir) {
        state.decodeOutputDir = decodeOutputDir;
    }

    public boolean isDecodeNoResources() {
        return state.decodeNoResources;
    }

    public void setDecodeNoResources(boolean decodeNoResources) {
        state.decodeNoResources = decodeNoResources;
    }

    public boolean isDecodeNoSources() {
        return state.decodeNoSources;
    }

    public void setDecodeNoSources(boolean decodeNoSources) {
        state.decodeNoSources = decodeNoSources;
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
