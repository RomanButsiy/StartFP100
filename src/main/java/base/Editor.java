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
import java.util.LinkedList;

public class Editor extends JFrame implements RunnerListener  {

    private Experiment experiment;
    private ExperimentController experimentController;
    private JSplitPane splitPane;


    boolean untitled;
    final Base base;
    final Platform platform;
    private JMenu fileMenu;
    private JMenu toolsMenu;
    private JMenu recentExperimentsMenu;

    private static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    public Editor(Base iBase, File file, int[] storedLocation, int[] defaultLocation,  Platform platform, boolean untitled) {
        super("StartFP100");
        this.base = iBase;
        this.platform = platform;
        this.untitled = untitled;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                base.handleClose(Editor.this);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                base.handleActivated(Editor.this);
            }
        });

        buildMenuBar();

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        setSize(600, 480);
        setPlacement(storedLocation, defaultLocation);

        boolean loaded = handleOpenInternal(file);
        if (!loaded) experimentController = null;
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
            BaseInit.showWarning("Помилка", "Не вдалося створити експкримент.", e);
            return false;
        }
        experimentController = new ExperimentController(this, experiment);
        experiment.setUntitledAndNotSaved(untitled);
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
        recentExperimentsMenu = new JMenu("Відкрити нещодавні");
        SwingUtilities.invokeLater(this::rebuildRecentExperimentsMenu);
        fileMenu.add(recentExperimentsMenu);





        return fileMenu;

    }

    public void rebuildRecentExperimentsMenu() {
        recentExperimentsMenu.removeAll();
        for (JMenuItem recentExperimentMenuItem  : base.getRecentExperimentsMenuItems()) {
            recentExperimentsMenu.add(recentExperimentMenuItem);
        }
    }

    private void setPlacement(int[] storedLocation, int[] defaultLocation) {
        if (storedLocation.length > 5 && storedLocation[5] != 0) {
            setExtendedState(storedLocation[5]);
            setPlacement(defaultLocation);
        } else {
            setPlacement(storedLocation);
        }
    }

    private void setPlacement(int[] location) {
        setBounds(location[0], location[1], location[2], location[3]);
        if (location[4] != 0) {
            splitPane.setDividerLocation(location[4]);
        }
    }

    protected int[] getPlacement() {
        int[] location = new int[6];
        Rectangle bounds = getBounds();
        location[0] = bounds.x;
        location[1] = bounds.y;
        location[2] = bounds.width;
        location[3] = bounds.height;
        location[4] = splitPane.getDividerLocation();
        location[5] = getExtendedState() & MAXIMIZED_BOTH;
        return location;
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
