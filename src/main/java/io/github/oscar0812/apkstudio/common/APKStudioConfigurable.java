package io.github.oscar0812.apkstudio.common;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class APKStudioConfigurable implements Configurable {

    private JCheckBox forceBuildCheckBox;
    private JCheckBox forceDecodeCheckBox;
    private JTextField buildOutputDirTextField;
    private JTextField decodeOutputDirTextField;
    private JCheckBox decodeNoResourcesCheckBox;
    private JCheckBox decodeNoSourcesCheckBox;

    private APKStudioSettings getSettings() {
        return APKStudioSettings.getInstance();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "APKStudio Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(null);  // Set layout to null for absolute positioning

        int headerFontSize = 16;
        JLabel buildTitleLabel = new JLabel("APKTool Build");
        buildTitleLabel.setFont(new Font("SansSerif", Font.BOLD, headerFontSize));
        buildTitleLabel.setBounds(5, 5, 200, 25);
        panel.add(buildTitleLabel);

        forceBuildCheckBox = new JCheckBox("Force Build");
        forceBuildCheckBox.setBounds(5, 35, 200, 25);
        panel.add(forceBuildCheckBox);

        // Output Directory Label and TextField
        JLabel buildOutputDirLabel = new JLabel("Output Directory:");
        buildOutputDirLabel.setBounds(5, 65, 150, 25);
        panel.add(buildOutputDirLabel);

        buildOutputDirTextField = new JTextField("dist", 20);
        buildOutputDirTextField.setBounds(160, 65, 200, 25);
        panel.add(buildOutputDirTextField);

        JSeparator separator = new JSeparator();
        separator.setBounds(5, 95, 355, 10);
        panel.add(separator);

        JLabel decodeTitleLabel = new JLabel("APKTool Decode");
        decodeTitleLabel.setFont(new Font("SansSerif", Font.BOLD, headerFontSize));
        decodeTitleLabel.setBounds(5, 105, 200, 25);
        panel.add(decodeTitleLabel);

        forceDecodeCheckBox = new JCheckBox("Force Decode");
        forceDecodeCheckBox.setBounds(5, 135, 200, 25);
        panel.add(forceDecodeCheckBox);

        decodeNoResourcesCheckBox = new JCheckBox("Decode No Resources");
        decodeNoResourcesCheckBox.setBounds(5, 165, 200, 25);
        panel.add(decodeNoResourcesCheckBox);

        decodeNoSourcesCheckBox = new JCheckBox("Decode No Sources");
        decodeNoSourcesCheckBox.setBounds(5, 195, 200, 25);
        panel.add(decodeNoSourcesCheckBox);

        JLabel decodeOutputDirLabel = new JLabel("Output Directory:");
        decodeOutputDirLabel.setBounds(5, 225, 150, 25);
        panel.add(decodeOutputDirLabel);

        decodeOutputDirTextField = new JTextField("decoded", 20);
        decodeOutputDirTextField.setBounds(160, 225, 200, 25);
        panel.add(decodeOutputDirTextField);

        return panel;
    }

    @Override
    public boolean isModified() {
        APKStudioSettings settings = getSettings();
        return forceBuildCheckBox.isSelected() != settings.isForceBuild() ||
                forceDecodeCheckBox.isSelected() != settings.isForceDecode() ||
                !buildOutputDirTextField.getText().equals(settings.getBuildOutputDir()) ||
                !decodeOutputDirTextField.getText().equals(settings.getDecodeOutputDir()) ||
                decodeNoResourcesCheckBox.isSelected() != settings.isDecodeNoResources() ||
                decodeNoSourcesCheckBox.isSelected() != settings.isDecodeNoSources();
    }

    @Override
    public void apply() {
        APKStudioSettings settings = getSettings();
        settings.setForceBuild(forceBuildCheckBox.isSelected());
        settings.setForceDecode(forceDecodeCheckBox.isSelected());
        settings.setBuildOutputDir(buildOutputDirTextField.getText());
        settings.setDecodeOutputDir(decodeOutputDirTextField.getText());
        settings.setDecodeNoResources(decodeNoResourcesCheckBox.isSelected());
        settings.setDecodeNoSources(decodeNoSourcesCheckBox.isSelected());
    }

    @Override
    public void reset() {
        APKStudioSettings settings = getSettings();
        forceBuildCheckBox.setSelected(settings.isForceBuild());
        forceDecodeCheckBox.setSelected(settings.isForceDecode());
        buildOutputDirTextField.setText(settings.getBuildOutputDir());
        decodeOutputDirTextField.setText(settings.getDecodeOutputDir());
        decodeNoResourcesCheckBox.setSelected(settings.isDecodeNoResources());
        decodeNoSourcesCheckBox.setSelected(settings.isDecodeNoSources());
    }
}
