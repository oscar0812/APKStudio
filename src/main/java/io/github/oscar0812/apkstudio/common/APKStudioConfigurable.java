package io.github.oscar0812.apkstudio.common;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class APKStudioConfigurable implements Configurable {

    private static APKStudioSettings settings;
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
        return forceBuildCheckBox.isSelected() != settings.isForceB() ||
                forceDecodeCheckBox.isSelected() != settings.isForceD() ||
                !buildOutputDirTextField.getText().equals(settings.getOutputDirB()) ||
                !decodeOutputDirTextField.getText().equals(settings.getOutputDirD()) ||
                decodeNoResourcesCheckBox.isSelected() != settings.isNoResources() ||
                decodeNoSourcesCheckBox.isSelected() != settings.isNoSources();
    }

    @Override
    public void apply() {
        APKStudioSettings settings = getSettings();
        settings.setForceB(forceBuildCheckBox.isSelected());
        settings.setForceD(forceDecodeCheckBox.isSelected());
        settings.setOutputDirB(buildOutputDirTextField.getText());
        settings.setOutputDirD(decodeOutputDirTextField.getText());
        settings.setNoResources(decodeNoResourcesCheckBox.isSelected());
        settings.setNoSources(decodeNoSourcesCheckBox.isSelected());
    }

    @Override
    public void reset() {
        APKStudioSettings settings = getSettings();
        forceBuildCheckBox.setSelected(settings.isForceB());
        forceDecodeCheckBox.setSelected(settings.isForceD());
        buildOutputDirTextField.setText(settings.getOutputDirB());
        decodeOutputDirTextField.setText(settings.getOutputDirD());
        decodeNoResourcesCheckBox.setSelected(settings.isNoResources());
        decodeNoSourcesCheckBox.setSelected(settings.isNoSources());
    }
}
