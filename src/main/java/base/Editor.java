package base;

import base.platforms.Platform;
import base.processing.Experiment;
import base.processing.ExperimentController;
import base.view.*;
import base.view.ExperimentSettings;
import libraries.MenuScroller;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static base.helpers.BaseHelper.copyFile;
import static libraries.Theme.scale;

public class Editor extends JFrame implements RunnerListener  {

    private File file;
    private Experiment experiment;
    private ExperimentController experimentController;
    private JSplitPane splitPane;

    private final EditorToolbar toolbar;
    private ArrayList<DiagramTab> tabs = new ArrayList<>();

    boolean untitled;
    final Base base;
    final Platform platform;
    private final Box upper;
    private JMenu fileMenu;
    static JMenu toolbarMenu;
    private JMenu toolsMenu;
    private JMenu recentExperimentsMenu;
    private JMenuItem saveAsMenuItem;
    private static JMenu portMenu;
    private static JMenu rateMenu;
    private static JMenu signalMenu;
    private static JMenu experimentMenu;

    final EditorHeader header;
    EditorConsole console;
    EditorLineStatus lineStatus;
    private JPanel diagramPanel;

    private static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    public static final String [] signals = { "Синусоїда", "Трапеція", "Трикутник", "Інший" };
    private static final String [] rates = {"300", "1200", "2400", "4800", "9600", "19200", "38400", "57600", "74880", "115200", "230400"};
    private int currentTabIndex;
    private ExperimentSettings experimentSettings;

    public Editor(Base iBase, File file, int[] storedLocation, int[] defaultLocation,  Platform platform, boolean untitled) {
        super("StartFP100");
        this.base = iBase;
        this.platform = platform;
        this.untitled = untitled;
        this.file = file;

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
            public void windowDeactivated(WindowEvent e) {
                List<Component> toolsMenuItemsToRemove = new LinkedList<>();
                for (Component menuItem : toolsMenu.getMenuComponents()) {
                    if (menuItem instanceof JComponent) {
                        Object removeOnWindowDeactivation = ((JComponent) menuItem).getClientProperty("removeOnWindowDeactivation");
                        if (removeOnWindowDeactivation != null && Boolean.parseBoolean(removeOnWindowDeactivation.toString())) {
                            toolsMenuItemsToRemove.add(menuItem);
                        }
                    }
                }
                for (Component menuItem : toolsMenuItemsToRemove) {
                    toolsMenu.remove(menuItem);
                }
                toolsMenu.remove(signalMenu);
                toolsMenu.remove(portMenu);
                toolsMenu.remove(rateMenu);
            }
        });

        buildMenuBar();
        Container contentPain = getContentPane();
        contentPain.setLayout(new BorderLayout());
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        contentPain.add(pane, BorderLayout.CENTER);
        Box box = Box.createVerticalBox();
        upper = Box.createVerticalBox();
        if (toolbarMenu == null) {
            toolbarMenu = new JMenu();
            base.rebuildToolbarMenu(toolbarMenu);
        }
        JPanel consolePanel = new JPanel();
        toolbar = new EditorToolbar(this);
        header = new EditorHeader(this);
        upper.add(toolbar);
        upper.add(header);
        consolePanel.setLayout(new BorderLayout());
        console = new EditorConsole(base);
        console.setName("console");
        console.setBorder(null);
        consolePanel.add(console, BorderLayout.CENTER);
        lineStatus = new EditorLineStatus();
        consolePanel.add(lineStatus, BorderLayout.SOUTH);
        diagramPanel = new JPanel(new BorderLayout());
        upper.add(diagramPanel);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, consolePanel);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(1D);
        splitPane.setBorder(null);
        splitPane.setDividerSize(scale(splitPane.getDividerSize()));
        splitPane.setMinimumSize(scale(new Dimension(600, 100)));
        box.add(splitPane);
        pane.add(box);
        setMinimumSize(scale(new Dimension(
                PreferencesData.getInteger("window.size.width.min"),
                PreferencesData.getInteger("window.size.height.min"))));
        setPlacement(storedLocation, defaultLocation);
        pack();
        boolean loaded = handleOpenInternal(file);
        if (!loaded) experimentController = null;
        EditorConsole.setCurrentEditorConsole(console);
        if (base.editors.isEmpty()) base.handleTestConnection(this);
    }

    public void applyPreferences() {
        for (DiagramTab tab: tabs) {
            //tab.applyPreferences();
        }
        console.applyPreferences();
    }

    private boolean handleOpenInternal(File experimentFile) {
        String fileName = experimentFile.getName();
        File file = Experiment.checkExperimentFile(experimentFile);
        String properParent = fileName.substring(0, fileName.length() - 4);
        if (file == null) {
            if (!fileName.endsWith(".fim")) {
                BaseInit.showWarning("Вибрано неправильний файл", "StartFP100 може відкривати лише власні експерименти\n" +
                                           "та інші файли, що закінчуються на .ino", null);
                return false;
            } else {
                Object[] options = { "OK", "Скасувати" };
                String prompt = "Файл " + fileName + " повинен бути всередині\n" +
                                "папки експерименту " +  properParent + ".\n" +
                                "Створити цю папку, перемістити файл та продовжити?";

                int result = JOptionPane.showOptionDialog(this, prompt, "Перемістити?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (result != JOptionPane.YES_OPTION) {
                    return false;
                }
                File properFolder = new File(experimentFile.getParent(), properParent);
                if (properFolder.exists()) {
                    BaseInit.showWarning("Помилка", "Папка з назвою " + properParent + " вже існує. Не вдається відкрити експеримент.", null);
                    return false;
                }
                if (!properFolder.mkdirs()) {
                    BaseInit.showWarning("Помилка", "Не вдалося створити папку експерименту.", null);
                    return false;
                }
                File properFimFile = new File(properFolder, experimentFile.getName());
                try {
                    copyFile(experimentFile, properFimFile);
                } catch (IOException e) {
                    BaseInit.showWarning("Помилка", "Не вдалося скопіювати у потрібне місце.", e);
                    return false;
                }
                experimentFile.delete();
                file = properFimFile;
            }
        }
        try {
            experiment = new Experiment(file);
        } catch (IOException e) {
            BaseInit.showWarning("Помилка", "Не вдалося створити експкримент.", e);
            return false;
        }
        experimentController = new ExperimentController(this, experiment);
        experiment.setUntitledAndNotSaved(untitled);
        setTitle("StartFP100 | " + properParent);
        createTabs();
        untitled = false;
        return true;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    private void createTabs() {
        tabs.clear();
        currentTabIndex = -1;
        DiagramTab tab = new DiagramTab(this, "Якись_графік");
        DiagramTab tab2 = new DiagramTab(this, "Якись_графік_2");
        tabs.add(tab);
        tabs.add(tab2);
        selectTab(0);
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = buildFileMenu();
        fileMenu.addMenuListener(new StubMenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                List<Component> components = Arrays.asList(fileMenu.getMenuComponents());
                if (!components.contains(experimentMenu)) {
                    fileMenu.insert(experimentMenu, 3);
                }
                fileMenu.revalidate();
                validate();
            }
        });
        menuBar.add(fileMenu);
        final JMenu experimentMenu = new JMenu("Експеримент");
        experimentMenu.setMnemonic(KeyEvent.VK_S);
        buildExperimentMenu(experimentMenu);
        menuBar.add(experimentMenu);
        final JMenu toolsMenu = buildToolsMenu();
        toolsMenu.addMenuListener(new StubMenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                List<Component> components = Arrays.asList(toolsMenu.getMenuComponents());
                if (!components.contains(signalMenu)) {
                    toolsMenu.insert(signalMenu, 1);
                }
                if (!components.contains(rateMenu)) {
                    toolsMenu.insert(rateMenu, 3);
                }
                if (!components.contains(portMenu)) {
                    toolsMenu.insert(portMenu, 4);
                }
                toolsMenu.revalidate();
                validate();
            }
        });
        menuBar.add(toolsMenu);
        menuBar.add(buildHelpMenu());
        setJMenuBar(menuBar);

    }

    private JMenu buildHelpMenu() {
        JMenu menu = new JMenu("Допомога");
        menu.setMnemonic(KeyEvent.VK_H);
        JMenuItem item = newJMenuItem("Допомога", 'G');
        item.addActionListener(event -> base.showHelp());
        menu.add(item);
        item = new JMenuItem("Перейти на StartFP100 (git)");
        item.addActionListener(event -> base.openURL("https://github.com/RomanButsiy/StartFP100"));
        menu.add(item);
        menu.addSeparator();
        item = newJMenuItem("Про StartFP100", 'A');
        item.addActionListener(event -> base.handleAbout());
        menu.add(item);
        return menu;
    }

    private JMenu buildToolsMenu() {
        toolsMenu = new JMenu("Інструменти");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        JMenuItem item = newJMenuItem("Налаштування експерименту", 'E');
        item.addActionListener(event -> handleExperimentSettings());
        toolsMenu.add(item);
        if (signalMenu == null) {
            signalMenu = new JMenu("Форма сигналу");
        }
        populateSignalMenu();
        toolsMenu.add(signalMenu);
        MenuScroller.setScrollerFor(signalMenu);
        toolsMenu.addSeparator();
        if (rateMenu == null) {
            rateMenu = new JMenu("Швидкість");
        }
        populateRateMenu();
        toolsMenu.add(rateMenu);
        MenuScroller.setScrollerFor(rateMenu);
        if (portMenu == null) {
            portMenu = new JMenu("Порт");
        }
        populatePortMenu();
        toolsMenu.add(portMenu);
        MenuScroller.setScrollerFor(portMenu);
        toolsMenu.addSeparator();
        item = newJMenuItem("Інформація про пристрій", 'I');
        item.addActionListener(event -> base.handleDeviceInformation());
        toolsMenu.add(item);
        item = newJMenuItem("Перевірити з'єднання", 'T');
        item.addActionListener(event -> base.handleTestModulesConnection());
        toolsMenu.add(item);
        item = newJMenuItem("Налаштування", 'D');
        item.addActionListener(event -> handleSettings());
        toolsMenu.add(item);
        toolsMenu.addMenuListener(new StubMenuListener() {
            public void menuSelected(MenuEvent e) {
                populatePortMenu();
                populateSignalMenu();
                for (Component c : toolsMenu.getMenuComponents()) {
                    if ((c instanceof JMenu) && c.isVisible()) {
                        JMenu menu = (JMenu)c;
                        String name = menu.getText();
                        if (name == null) continue;
                        String baseName = name;
                        int index = name.indexOf(':');
                        if (index > 0) baseName = name.substring(0, index);
                        String sel = null;
                        int count = menu.getItemCount();
                        for (int i=0; i < count; i++) {
                            JMenuItem item = menu.getItem(i);
                            if (item != null && item.isSelected()) {
                                sel = item.getText();
                                if (sel != null) break;
                            }
                        }
                        if (sel == null) {
                            if (!name.equals(baseName)) menu.setText(baseName);
                        } else {
                            if (sel.length() > 50) sel = sel.substring(0, 50) + "...";
                            String newName = baseName + ": \"" + sel + "\"";
                            if (!name.equals(newName)) menu.setText(newName);
                        }
                    }
                }
            }
        });
        return toolsMenu;
    }

    private void handleSettings() {
    }

    private void populateSignalMenu() {
        signalMenu.removeAll();
        boolean isLabel = true;
        int selectedSignal = PreferencesData.getInteger("signal.form");
        for (int i = 0; i < signals.length; i++) {
            if (isLabel) {
                JMenuItem item = new JMenuItem("Форма сигналу");
                item.setEnabled(false);
                signalMenu.add(item);
                signalMenu.addSeparator();
                isLabel = false;
            }
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(signals[i]);
            int finalI = i;
            item.addActionListener(event -> {
                selectSignalForm(finalI);
            });
            item.setSelected(i == selectedSignal);
            signalMenu.add(item);
        }
        signalMenu.setEnabled(signalMenu.getMenuComponentCount() > 0);
    }

    private void populateRateMenu() {
        rateMenu.removeAll();
        boolean isLabel = true;
        String selectedRate = PreferencesData.get("serial.port.rate");
        for (String rate : rates) {
            if (isLabel) {
                JMenuItem item = new JMenuItem("Швидкість");
                item.setEnabled(false);
                rateMenu.add(item);
                rateMenu.addSeparator();
                isLabel = false;
            }
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(rate);
            item.addActionListener(event -> {
                selectRate(rate);
            });
            item.setSelected(rate.equals(selectedRate));
            rateMenu.add(item);
        }
        rateMenu.setEnabled(rateMenu.getMenuComponentCount() > 0);
    }

    private void populatePortMenu() {
        portMenu.removeAll();
        boolean isLabel = true;
        String selectedPort = PreferencesData.get("serial.port");
        List<String> ports = Base.getDiscoveryManager().discovery();
        for (String port : ports) {
            if (isLabel) {
                JMenuItem item = new JMenuItem("Послідовні порти");
                item.setEnabled(false);
                portMenu.add(item);
                portMenu.addSeparator();
                isLabel = false;
            }
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(port);
            item.addActionListener(event -> {
                selectSerialPort(port);
            });
            item.setSelected(port.equals(selectedPort));
            portMenu.add(item);
        }
        portMenu.setEnabled(portMenu.getMenuComponentCount() > 0);
    }

    private void selectAction(JMenu menu, String detail) {
        if (menu == null) return;
        if (detail == null) return;
        JCheckBoxMenuItem selection = null;
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            if (!(menuItem instanceof JCheckBoxMenuItem)) {
                continue;
            }
            JCheckBoxMenuItem checkBoxMenuItem = ((JCheckBoxMenuItem) menuItem);
            checkBoxMenuItem.setState(false);
            if (detail.equals(checkBoxMenuItem.getText())) selection = checkBoxMenuItem;
        }
        if (selection != null) selection.setState(true);
    }

    private void selectSerialPort(String port) {
        selectAction(portMenu, port);
        base.selectSerialPort(port);
    }

    public void selectSerialPort() {
        lineStatus.setPort(PreferencesData.get("serial.port"));
        lineStatus.repaint();
    }

    private void selectSignalForm(int signal) {
        selectAction(signalMenu, signals[signal]);
        BaseInit.selectSignalForm(signal);
    }

    private void selectRate(String rate) {
        selectAction(rateMenu, rate);
        BaseInit.selectRate(rate);
    }

    public void handleExperimentSettings() {
        toolbar.activateSettings();
        ExperimentSettings experimentSettings = new ExperimentSettings(Editor.this);
        experimentSettings.setLocationRelativeTo(Editor.this);
        experimentSettings.setVisible(true);
        //JOptionPane.showMessageDialog(this, "Тут мають бути налаштування", "Налаштування", JOptionPane.INFORMATION_MESSAGE);
        toolbar.deactivateSettings();
    }

    private void buildExperimentMenu(JMenu experimentMenu) {
        experimentMenu.removeAll();
        JMenuItem item = newJMenuItem("Розпочати експеримент", 'R');
        item.addActionListener(event -> handleRun());
        experimentMenu.add(item);
        item = newJMenuItem("Зупинити експеримент", 'U');
        item.addActionListener(event -> handleStop());
        experimentMenu.add(item);
        experimentMenu.addSeparator();
        item = newJMenuItem("Відкрити папку з експериментом", 'K');
        item.addActionListener(event -> Base.openFolder(experiment.getFolder()));
        experimentMenu.add(item);
        item.setEnabled(Base.openFolderAvailable());
    }

    public void handleStop() {
        if (!experiment.isExperimentRunning()) return;
        toolbar.deactivateRun();
        toolbar.activateStop();
        experiment.setExperimentRunning(false);
        setLineStatusText("Експеримент зупинено");
        JOptionPane.showMessageDialog(this, "Експеримент зупинено", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
        toolbar.deactivateStop();
    }

    public void handleRun() {
        if (experiment.isExperimentRunning()) return;
        toolbar.activateRun();
        experiment.setExperimentRunning(true);
        setLineStatusText("Експеримент запущено");
        JOptionPane.showMessageDialog(this, "Експеримент запущено", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setLineStatusText(String s) {
        lineStatus.setText(s);
        lineStatus.repaint();
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
        if (experimentMenu == null) {
            experimentMenu = new JMenu("Експерименти");
            MenuScroller.setScrollerFor(experimentMenu);
            base.addExperiments(experimentMenu, BaseInit.getExperimentsFolder());
        }
        fileMenu.add(experimentMenu);
        item = Editor.newJMenuItem("Закрити", 'W');
        item.addActionListener(event -> base.handleClose(Editor.this));
        fileMenu.add(item);
        saveAsMenuItem = newJMenuItemShift("Зберегти як...", 'S');
        saveAsMenuItem.addActionListener(event -> handleSaveAs());
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        item = newJMenuItem("Налаштування", ',');
        item.addActionListener(event -> base.handlePrefs());
        fileMenu.add(item);
        fileMenu.addSeparator();
        item = newJMenuItem("Вихід", 'Q');
        item.addActionListener(event -> base.handleQuit());
        fileMenu.add(item);
        return fileMenu;
    }

    private void handleSaveAs() {
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
        System.out.println(message);
    }

    public ExperimentController getExperimentController() {
        return experimentController;
    }

    static public JMenuItem newJMenuItemShift(String title, int what) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(what, SHORTCUT_KEY_MASK | ActionEvent.SHIFT_MASK));
        return menuItem;
    }

    public List<DiagramTab> getTabs() {
        return tabs;
    }

    public void selectTab(final int index) {
        currentTabIndex = index;
        header.rebuild();
        SwingUtilities.invokeLater(() -> {
            diagramPanel.removeAll();
            diagramPanel.add(tabs.get(index), BorderLayout.CENTER);
            tabs.get(index).requestFocusInWindow();
            diagramPanel.revalidate();
            diagramPanel.repaint();
        });
    }


}
