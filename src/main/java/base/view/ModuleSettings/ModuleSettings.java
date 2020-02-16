package base.view.ModuleSettings;

import base.Editor;
import base.PreferencesData;
import base.view.SearchModules.SearchModules;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

public class ModuleSettings extends JDialog implements ItemListener {

    private JPanel rootPanel;
    private JButton cancelButton;
    private JButton okButton;
    private JButton searchButton;
    private JCheckBox useCRC;
    private JComboBox<String> rate;
    private JComboBox<String>  dataType;
    private JScrollPane scrollModules;
    private JPanel modulesPanel;
    private JLabel someText;
    private JButton applyButton;

    private String[] typeData = {"Технічні одиниці", "% від повного діапазону", "Дод. шістнадцятковий код"};

    public ModuleSettings(Editor editor) {
        super(editor);
        setTitle("Налаштування модулів");
        applyButton.setEnabled(false);
        useCRC.setSelected(PreferencesData.getBoolean("use.CRC"));
        for(String str : Editor.rates) {
            rate.addItem(str);
        }
        rate.setSelectedItem(PreferencesData.get("serial.port.rate"));
        for(String str : typeData) {
            dataType.addItem(str);
        }
        dataType.setSelectedItem(PreferencesData.getInteger("signal.type"));
        dataType.addItemListener(this);
        rate.addItemListener(this);
        cancelButton.addActionListener(actionEvent -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        okButton.addActionListener(actionEvent -> {
            if (enabledApplyButton()) apply();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        applyButton.addActionListener(actionEvent -> apply());
        searchButton.addActionListener(actionEvent -> {
            SearchModules searchModules = new SearchModules(editor);
            searchModules.setLocationRelativeTo(this);
            searchModules.setVisible(true);
        });
        useCRC.addActionListener(e -> applyButton.setEnabled(enabledApplyButton()));
        add(rootPanel);
        setModal(true);
        setResizable(false);
        pack();
    }

    private void apply() {
        PreferencesData.set("serial.port.rate", Editor.rates[rate.getSelectedIndex()]);
        PreferencesData.setBoolean("use.CRC", useCRC.isSelected());
        PreferencesData.setInteger("signal.type", dataType.getSelectedIndex());
        PreferencesData.save();
        applyButton.setEnabled(false);
    }

    private boolean enabledApplyButton() {
        return (PreferencesData.getInteger("signal.type") != dataType.getSelectedIndex() ||
                PreferencesData.getBoolean("use.CRC") != useCRC.isSelected() ||
                !PreferencesData.get("serial.port.rate").equals(Editor.rates[rate.getSelectedIndex()]));
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.DESELECTED) return;
        applyButton.setEnabled(enabledApplyButton());
    }
}
