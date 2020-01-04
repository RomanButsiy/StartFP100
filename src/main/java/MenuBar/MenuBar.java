package MenuBar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class MenuBar extends JMenuBar {

    private ToolsMenu toolsMenu;
    private ResourceBundle bundle;

    public MenuBar(ResourceBundle bundle) {
        this.bundle = bundle;
        toolsMenu = new ToolsMenu(bundle);
        add(createFileMenu());
        add(toolsMenu.createToolsMenu());
    }
    private JMenu createFileMenu() {
        JMenu file = new JMenu(bundle.getString("menuFile"));
        JMenuItem ChangeUser = new JMenuItem(new ChangeUserAction());
        JMenuItem Exit = new JMenuItem(new ExitAction());
        file.add(ChangeUser);
        file.addSeparator();
        file.add(Exit);
        return file;
    }

    PortAndSpeed getPortAndSpeed() {
        return toolsMenu.getPortAndSpeed();
    }

    private class ExitAction extends AbstractAction {
        ExitAction() {
            putValue(NAME, bundle.getString("menuExit"));
        }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    private class ChangeUserAction extends AbstractAction {
        ChangeUserAction() {
            putValue(NAME, bundle.getString("menuOpenFile"));
        }
        public void actionPerformed(ActionEvent e) {
            }
        }

}
