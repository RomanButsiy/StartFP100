package base.view.Settings;

import base.Base;
import base.Editor;
import base.PreferencesData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;

public class Settings extends MainForm {

    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JButton button2Button;
    private JPanel settings;
    private JPanel db;
    private JCheckBox useListSerialPorts;
    private JTextField experimentsFolder;
    private JButton setExperimentsFolder;
    private JComboBox comboBoxLanguages;
    private JLabel labelPreferences1;
    private JLabel labelPreferences3;
    private JLabel labelPreferences2;
    private JLabel labelLanguages;
    private JLabel labelReload;
    private JLabel labelExperimentsFolder;

    private final boolean useListSerialPortsDef = PreferencesData.getBoolean("general.use.native.list.serial", true);
    private final boolean nativeException = PreferencesData.getBoolean("runtime.native.exception", true);

    public Settings(Editor editor) {
        super(editor, "Налаштування", true, true);
        initButtons("Скасувати", "Гаразд");
        setTabTitle(db, "База даних");
        setTabTitle(settings, "Налаштування");
        labelPreferences1.setForeground(Color.GRAY);
        labelPreferences2.setText(PreferencesData.getPreferencesFile().getAbsolutePath());
        labelPreferences2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelPreferences2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                preferencesFileLabelMousePressed(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                preferencesFileLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                preferencesFileLabelMouseEntered(evt);
            }
        });
        labelPreferences2.setFocusable(true);
        labelPreferences3.setForeground(Color.GRAY);
        useListSerialPorts.setSelected(useListSerialPortsDef);
        useListSerialPorts.setEnabled(!nativeException);
        setViewPanel(rootPanel);
    }

    @Override
    public void cancelAction() {
        windowClose();
    }

    @Override
    public void okAction() {
        if (useListSerialPortsDef != useListSerialPorts.isSelected()) {
            PreferencesData.setBoolean("general.use.native.list.serial", useListSerialPorts.isSelected());
            if (!nativeException) {
                PreferencesData.setBoolean("runtime.general.use.native.list.serial", useListSerialPorts.isSelected());
            }
            PreferencesData.save();
        }
        windowClose();
    }

    private void windowClose() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void setTabTitle(JPanel panel, String title) {
        for(int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (SwingUtilities.isDescendingFrom(panel, tabbedPane.getComponentAt(i))) {
                tabbedPane.setTitleAt(i, title);
                break;
            }
        }
    }

    private void preferencesFileLabelMouseEntered(java.awt.event.MouseEvent evt) {
        labelPreferences2.setForeground(new Color(0, 0, 140));
    }

    private void preferencesFileLabelMouseExited(java.awt.event.MouseEvent evt) {
        labelPreferences2.setForeground(new Color(76, 76, 76));
    }

    private void preferencesFileLabelMousePressed(java.awt.event.MouseEvent evt) {
        Base.openFolder(PreferencesData.getPreferencesFile().getParentFile());
    }

}
