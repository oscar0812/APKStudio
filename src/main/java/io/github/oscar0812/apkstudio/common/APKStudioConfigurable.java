package io.github.oscar0812.apkstudio.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class APKStudioConfigurable implements Configurable {

    private static APKStudioSettings settings;
    private JCheckBox forceBCheckBox;
    private JCheckBox forceDCheckBox;
    private JTextField outputDirBField;
    private JTextField outputDirDField;
    private JCheckBox noResourcesCheckBox;
    private JCheckBox noSourcesCheckBox;

    private APKStudioSettings getSettings() {
        return APKStudioSettings.getInstance();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "APKStudio Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {

        forceBCheckBox = new JCheckBox("Force Build", getSettings().isForceB());
        forceDCheckBox = new JCheckBox("Force Decode", getSettings().isForceD());
        outputDirBField = new JTextField(getSettings().getOutputDirB());
        outputDirDField = new JTextField(getSettings().getOutputDirD());
        noResourcesCheckBox = new JCheckBox("No Resources", getSettings().isNoResources());
        noSourcesCheckBox = new JCheckBox("No Sources", getSettings().isNoSources());

        int height = 4;
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createField("Force Build:", forceBCheckBox));
        panel.add(Box.createVerticalStrut(height));
        panel.add(createField("Force Decode:", forceDCheckBox));

        JSeparator separator = new JSeparator();
        panel.add(separator);
        panel.add(Box.createVerticalStrut(height));

        panel.add(createField("Output Directory (Build):", outputDirBField));
        panel.add(Box.createVerticalStrut(height));
        panel.add(createField("Output Directory (Decode):", outputDirDField));
        panel.add(Box.createVerticalStrut(height));
        panel.add(createField("No Resources:", noResourcesCheckBox));
        panel.add(Box.createVerticalStrut(height));
        panel.add(createField("No Sources:", noSourcesCheckBox));

        return panel;
    }

    private JPanel createField(String labelText, JComponent component) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        if (!(component instanceof JCheckBox)) {
            JLabel label = new JLabel(labelText);
            label.setPreferredSize(new Dimension(180, label.getPreferredSize().height));
            fieldPanel.add(label);
        }

        fieldPanel.add(component);

        return fieldPanel;
    }

    @Override
    public boolean isModified() {
        APKStudioSettings settings = getSettings();
        return forceBCheckBox.isSelected() != settings.isForceB() ||
                forceDCheckBox.isSelected() != settings.isForceD() ||
                !outputDirBField.getText().equals(settings.getOutputDirB()) ||
                !outputDirDField.getText().equals(settings.getOutputDirD()) ||
                noResourcesCheckBox.isSelected() != settings.isNoResources() ||
                noSourcesCheckBox.isSelected() != settings.isNoSources();
    }

    @Override
    public void apply() {
        APKStudioSettings settings = getSettings();
        settings.setForceB(forceBCheckBox.isSelected());
        settings.setForceD(forceDCheckBox.isSelected());
        settings.setOutputDirB(outputDirBField.getText());
        settings.setOutputDirD(outputDirDField.getText());
        settings.setNoResources(noResourcesCheckBox.isSelected());
        settings.setNoSources(noSourcesCheckBox.isSelected());
    }

    @Override
    public void reset() {
        APKStudioSettings settings = getSettings();
        forceBCheckBox.setSelected(settings.isForceB());
        forceDCheckBox.setSelected(settings.isForceD());
        outputDirBField.setText(settings.getOutputDirB());
        outputDirDField.setText(settings.getOutputDirD());
        noResourcesCheckBox.setSelected(settings.isNoResources());
        noSourcesCheckBox.setSelected(settings.isNoSources());
    }
}
