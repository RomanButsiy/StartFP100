package base.view.ModuleSettings;

import base.Editor;
import base.PreferencesData;
import base.helpers.SendOne;
import base.processing.Module;
import base.view.SearchModules.SearchModules;
import libraries.I7000;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.util.List;

import static base.helpers.BaseHelper.LittleBitPreferencesModuleTest;
import static base.helpers.BaseHelper.splitToNChar;

public class ModuleSettings extends JDialog implements ItemListener {

    private final Editor editor;
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
    private JPanel modulePanel;

    private String[] typeData = {"Технічні одиниці", "% від повного діапазону", "Дод. шістнадцятковий код"};

    public ModuleSettings(Editor editor) {
        super(editor);
        this.editor = editor;
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
        dataType.addItemListener(this);
        rate.addItemListener(this);
        dataType.setSelectedIndex(PreferencesData.getInteger("signal.type"));
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
            if (searchModules.getList().size() != 0) updateModulesList(searchModules.getList());
        });
        useCRC.addActionListener(e -> applyButton.setEnabled(enabledApplyButton()));
        if (editor.getExperiment().getModules().size() != 0) createModulesPanel();
        add(rootPanel);
        setModal(true);
        setResizable(false);
        pack();
    }

    public Editor getEditor() {
        return editor;
    }

    private void createModulesPanel() {
        someText.setVisible(false);
        List<Module> modules = editor.getExperiment().getModules();
        if (modules.size() == 0) {
            scrollModules.setPreferredSize(new Dimension(-1, 30));
            someText.setVisible(true);
            modulePanel.setBackground(new Color(255,255,255));
            modulePanel.add(someText);
            return;
        }
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < modules.size(); i++) {
            constraints.gridy = i;
            modulePanel.add(new ModulePanel(this, modules.get(i)), constraints);
        }
        modulePanel.setBackground(new Color(242,242,242));
        switch (modules.size()) {
            case 1 : scrollModules.setPreferredSize(new Dimension(-1, 50)); break;
            case 2 : scrollModules.setPreferredSize(new Dimension(-1, 100)); break;
            case 3 : scrollModules.setPreferredSize(new Dimension(-1, 150)); break;
            default: scrollModules.setPreferredSize(new Dimension(-1, 200));
        }
    }

    private void updateModulesList(List<String[]> list) {
        PreferencesData.setInteger("number.of.modules", list.size());
        for (int i = 0; i < list.size(); i++) {
            PreferencesData.set(String.format("module.%s.id", i), list.get(i)[0]);
            PreferencesData.set(String.format("module.%s.config", i), list.get(i)[1]);
            PreferencesData.set(String.format("module.%s.type", i), list.get(i)[2]);
            if (list.get(i)[2].contains(PreferencesData.get("type.dac.module"))) {
                PreferencesData.set(String.format("module.%s.id", PreferencesData.get("type.dac.module")), list.get(i)[0]);
            }
        }
        PreferencesData.save();
        LittleBitPreferencesModuleTest(editor);
        rebuildSettings();
    }

    private void apply() {
        List<Module> modules = editor.getExperiment().getModules();
        if (PreferencesData.getBoolean("use.CRC") != useCRC.isSelected() || !PreferencesData.get("serial.port.rate").equals(Editor.rates[rate.getSelectedIndex()])) {
            JOptionPane.showMessageDialog(editor, "Для того щоб змінити налаштування, потрібно\nзамкнути вивід модуля INIT*\nна землю і натиснути \"OK\"", "Попередження", JOptionPane.ERROR_MESSAGE);
        }
        boolean flag = false;
        for (Module module : modules) {
            if (!module.isReady()) continue;
            String[] confBytes = splitToNChar(module.getConfig(), 2);
            String newRate = String.format("%02X", rate.getSelectedIndex() + 3);
            byte FF = hexToByte(confBytes[3]);
            if (useCRC.isSelected())
                FF |= 0x40;
            else
                FF &= 0xBF;
            FF &= 0xFC;
            FF |= dataType.getSelectedIndex();
            SendOne sendOne = new SendOne(editor, I7000.setModuleConfiguration(confBytes[0], confBytes[1], newRate, bytesToHex(FF)));
            if (sendOne.getResult() == null) {
                editor.statusError("Модуль: " + confBytes[0] + " -> Не відповідає");
                continue;
            }
            if (sendOne.getResult().startsWith("?")) {
                editor.statusError("Модуль: " + confBytes[0] + " -> Не вдалося змінити налаштування");
                continue;
            }
            module.setConfig(confBytes[0] + confBytes[1] + newRate + bytesToHex(FF));
            PreferencesData.set(String.format("module.%s.config", module.getId()), module.getConfig());
            flag = true;
        }
        if (!flag) {
            JOptionPane.showMessageDialog(editor, "Не вдалося змінити налаштування", "Попередження", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PreferencesData.set("serial.port.rate", Editor.rates[rate.getSelectedIndex()]);
        PreferencesData.setBoolean("use.CRC", useCRC.isSelected());
        PreferencesData.setInteger("signal.type", dataType.getSelectedIndex());
        PreferencesData.save();
        applyButton.setEnabled(false);
    }

    public static String bytesToHex(byte bytes) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[2];
            int v = bytes & 0xFF;
            hexChars[0] = HEX_ARRAY[v >>> 4];
            hexChars[1] = HEX_ARRAY[v & 0x0F];
        return new String(hexChars);
    }

    public byte hexToByte(String hexString) {
        return (byte) ((Character.digit(hexString.charAt(0), 16) << 4) + Character.digit(hexString.charAt(1), 16));
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

    public void rebuildSettings() {
        modulePanel.removeAll();
        createModulesPanel();
        pack();
        repaint();
        revalidate();
    }
}
