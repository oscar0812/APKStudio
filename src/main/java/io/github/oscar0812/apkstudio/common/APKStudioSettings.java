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
        public boolean forceB = false;
        public boolean forceD = false;

        public String outputDirB = "dist/name.apk";
        public String outputDirD = "decoded";

        public boolean noResources = false;
        public boolean noSources = false;
    }

    private static APKStudioSettings instance;

    public static APKStudioSettings getInstance() {
        if (instance == null) {
            instance = ApplicationManager.getApplication().getService(APKStudioSettings.class);
        }
        return instance;
    }

    private State state = new State();

    public boolean isForceB() {
        return state.forceB;
    }

    public void setForceB(boolean forceB) {
        state.forceB = forceB;
    }

    public boolean isForceD() {
        return state.forceD;
    }

    public void setForceD(boolean forceD) {
        state.forceD = forceD;
    }

    public String getOutputDirB() {
        return state.outputDirB;
    }

    public void setOutputDirB(String outputDirB) {
        state.outputDirB = outputDirB;
    }

    public String getOutputDirD() {
        return state.outputDirD;
    }

    public void setOutputDirD(String outputDirD) {
        state.outputDirD = outputDirD;
    }

    public boolean isNoResources() {
        return state.noResources;
    }

    public void setNoResources(boolean noResources) {
        state.noResources = noResources;
    }

    public boolean isNoSources() {
        return state.noSources;
    }

    public void setNoSources(boolean noSources) {
        state.noSources = noSources;
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
