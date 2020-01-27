package base;

import base.platforms.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class Editor extends JFrame implements RunnerListener  {

    boolean untitled;
    final Base base;
    final Platform platform;
    private JMenu fileMenu;
    private JMenu toolsMenu;

    private static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    public Editor(Base iBase, File file, int[] location, Platform platform) {
        super("StartFP100");
        this.base = iBase;
        this.platform = platform;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                base.handleClose(Editor.this);
            }
        });
        buildMenuBar();


        setVisible(true);
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = buildFileMenu();

        menuBar.add(fileMenu);


        setJMenuBar(menuBar);

    }

    private JMenu buildFileMenu() {
        JMenuItem item;
        fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        item = Editor.newJMenuItem("Відкрити...", 'O');
        item.addActionListener(event -> {
            try {
                base.handleOpenPrompt();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        fileMenu.add(item);
        return fileMenu;

    }

    static public JMenuItem newJMenuItem(String title, int what) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(what, SHORTCUT_KEY_MASK));
        return menuItem;
    }

    @Override
    public void statusError(String message) {
        System.err.println(message);
    }

    @Override
    public void statusError(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void statusNotice(String message) {
        System.out.println("Notice: " + message + "\n");
    }
}
