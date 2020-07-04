package base.view.Settings;

import base.Editor;
import javax.swing.*;
import java.awt.event.WindowEvent;

public class Settings extends MainForm {

    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JButton button2Button;
    private JPanel settings;
    private JPanel db;
    private JCheckBox checkBoxddddddddddddddddddddddddddddddddddddddCheckBox;

    public Settings(Editor editor) {
        super(editor, "Налаштування", true, true);
        initButtons("Гаразд", "Скасувати");
        setTabTitle(db, "База даних");
        setTabTitle(settings, "Налаштування");
        setViewPanel(rootPanel);
    }

    @Override
    public void cancelAction() {
        windowClose();
    }

    @Override
    public void okAction() {
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
