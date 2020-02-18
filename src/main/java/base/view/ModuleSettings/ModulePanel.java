package base.view.ModuleSettings;

import base.Editor;
import base.PreferencesData;
import base.processing.Module;

import javax.swing.*;
import java.util.List;

import static base.helpers.BaseHelper.LittleBitPreferencesModuleTest;

public class ModulePanel extends JPanel{
    private JPanel panel;
    private JLabel number;
    private JButton settingsButton;
    private JButton deleteButton;
    private JLabel id;
    private JLabel type;
    private JCheckBox isActiveCheckBox;

    public ModulePanel(ModuleSettings settings, Module module) {
        super();
        Editor editor = settings.getEditor();
        number.setText(module.getId() + 1 + ".");
        id.setText(String.format("ID = %s", module.getModuleId()));
        type.setText(String.format("Тип: %s", module.getType()));
        isActiveCheckBox.setSelected(module.isActive());
        settingsButton.setEnabled(module.isReady());
        deleteButton.addActionListener(e -> {
            int action = JOptionPane.showConfirmDialog(this, "Ви впевнені, що хочете видалити цей модуль",
                    String.format("Видалити модуль ID = %s", module.getModuleId()), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (action != JOptionPane.YES_OPTION) return;
            int numberOfModules = PreferencesData.getInteger("number.of.modules", 0);
            for (int i = 0; i < numberOfModules; i++) {
                PreferencesData.remove(String.format("module.%s.id", i));
                PreferencesData.remove(String.format("module.%s.config", i));
                PreferencesData.remove(String.format("module.%s.type", i));
                PreferencesData.remove(String.format("module.%s.active", i));
            }
            PreferencesData.setInteger("number.of.modules", numberOfModules - 1);
            editor.getExperiment().removeModule(module.getId());
            if (PreferencesData.get("runtime.dac.module", "").equals(module.getModuleId())) {
                PreferencesData.remove(String.format("module.%s.id", PreferencesData.get("type.dac.module")));
            }
            List<Module> list = editor.getExperiment().getModules();
            for (int i = 0; i < numberOfModules - 1; i++) {
                PreferencesData.set(String.format("module.%s.id", i), list.get(i).getModuleId());
                PreferencesData.set(String.format("module.%s.config", i), list.get(i).getConfig());
                PreferencesData.set(String.format("module.%s.type", i), list.get(i).getType());
                PreferencesData.setBoolean(String.format("module.%s.active", i), list.get(i).isActive());
            }
            PreferencesData.save();
            LittleBitPreferencesModuleTest(editor);
            settings.rebuildSettings();
        });
        isActiveCheckBox.addActionListener(l -> {
            module.setActive(isActiveCheckBox.isSelected());
            PreferencesData.setBoolean(String.format("module.%s.active", module.getId()), isActiveCheckBox.isSelected());
            PreferencesData.save();
        });
        settingsButton.addActionListener(e -> {JOptionPane.showMessageDialog(editor, "Тут мають бути налаштування", "Модуль: " + module.getModuleId(), JOptionPane.INFORMATION_MESSAGE);});
        add(panel);
    }

}
