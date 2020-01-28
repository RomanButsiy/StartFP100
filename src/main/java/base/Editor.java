package base;

import base.platforms.Platform;
import base.processing.Experiment;
import base.processing.ExperimentController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Editor extends JFrame implements RunnerListener  {

    Experiment experiment;
    ExperimentController experimentController;

    boolean untitled;
    final Base base;
    final Platform platform;
    private JMenu fileMenu;
    private JMenu toolsMenu;
    private JMenu recentSketchesMenu;

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

        boolean loaded = handleOpenInternal(file);
        if (!loaded) experimentController = null;
        setSize(600, 480);
        setLocationRelativeTo(null);
    }

    private boolean handleOpenInternal(File experimentFile) {
        String fileName = experimentFile.getName();
        File file = Experiment.checkExperimentFile(experimentFile);
        if (file == null) {
            // add some code
            return false;
        }
        try {
            experiment = new Experiment(file);
        } catch (IOException e) {
            BaseInit.showWarning("Помилка", "Could not create the sketch.", e);
            return false;
        }
        experimentController = new ExperimentController(this, experiment);
        untitled = false;
        return true;
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
        item = newJMenuItem("Новий експеримент", 'N');
        item.addActionListener(event -> {
            try {
                base.handleNew();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        fileMenu.add(item);
        item = Editor.newJMenuItem("Відкрити...", 'O');
        item.addActionListener(event -> {
            try {
                base.handleOpenPrompt();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        fileMenu.add(item);
        base.rebuildRecentExperimentsMenuItems();
        recentSketchesMenu = new JMenu("Відкрити нещодавні");
        SwingUtilities.invokeLater(this::rebuildRecentSketchesMenu);
        fileMenu.add(recentSketchesMenu);





        return fileMenu;

    }

    public void rebuildRecentSketchesMenu() {
        recentSketchesMenu.removeAll();
        for (JMenuItem recentSketchMenuItem  : base.getRecentExperimentsMenuItems()) {
            recentSketchesMenu.add(recentSketchMenuItem);
        }
    }

    public Experiment getExperiment() {
        return experiment;
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

    public ExperimentController getExperimentController() {
        return experimentController;
    }
}
