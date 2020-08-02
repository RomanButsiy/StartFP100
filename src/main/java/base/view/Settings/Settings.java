package base.view.Settings;

import base.Editor;
import base.PreferencesData;

import javax.swing.*;
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

}
