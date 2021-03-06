package base;

import base.ExportExcel.ExportExcel;
import base.helpers.BaseHelper;
import base.helpers.CheckModules;
import base.helpers.FileUtils;
import base.serial.DiscoveryManager;
import base.view.EditorConsole;
import base.view.Settings.Settings;
import libraries.I7000;
import libraries.MenuScroller;
import libraries.Theme;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static base.BaseInit.*;
import static base.helpers.BaseHelper.checkName;

public class Base {

    final List<Editor> editors = Collections.synchronizedList(new ArrayList<Editor>());
    private Editor activeEditor;
    private final List<JMenuItem> recentExperimentsMenuItems = new LinkedList<>();

    public Base(String[] args) throws Exception {
        BaseInit.initPlatform();
        BaseInit.getPlatform().init();
        String storeDirectory = Objects.requireNonNull(BaseInit.getSettingsFolder()).getAbsolutePath();
        System.out.println("Set store directory " + storeDirectory);
        BaseInit.initParameters(args);
        if (BaseInit.getExperimentsPath() == null) {
            File defaultFolder = BaseHelper.getDefaultExperimentsFolderOrPromptForIt();
            PreferencesData.set("experiments.path", defaultFolder.getAbsolutePath());
        }
        Theme.init();
        try {
            BaseInit.getPlatform().setLookAndFeel();
        } catch (Exception ignored) { }
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        I7000.useCRC = PreferencesData.getBoolean("use.CRC", false);
        if (nativeException == null) {
            PreferencesData.setBoolean("runtime.general.use.native.list.serial", PreferencesData.getBoolean("general.use.native.list.serial", false));
        }
        PreferencesData.setBoolean("runtime.native.exception", nativeException != null);
        restoreExperiment();
        if (editors.isEmpty()) handleNew();
    }

    static protected boolean openFolderAvailable() {
        return BaseInit.getPlatform().openFolderAvailable();
    }

    static public void openFolder(File file) {
        try {
            BaseInit.getPlatform().openFolder(file);
        } catch (Exception e) {
            showWarning("Проблема відкриття папки", "Не вдалося відкрити папку " + file.getAbsolutePath(), e);
        }
    }

    public void selectSerialPort(String port) {
        BaseInit.selectSerialPort(port);
        for (Editor editor : editors) {
            editor.selectSerialPort();
        }
    }

    public void addEditorFontResizeListeners(Component comp) {
        addEditorFontResizeMouseWheelListener(comp);
    }

    public void addEditorFontResizeMouseWheelListener(Component comp) {
        comp.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    this.handleFontSizeChange(1);
                } else {
                    this.handleFontSizeChange(-1);
                }
            } else {
                if (e.getComponent() != null && e.getComponent().getParent() != null) {
                    e.getComponent().getParent().dispatchEvent(e);
                }
            }
        });
    }

    public void handleFontSizeChange(int change) {
        String[] pieces = PreferencesData.get("editor.font").split(",");
        try {
            int newSize = Integer.parseInt(pieces[2]) + change;
            if (newSize < 4)
                newSize = 4;
            pieces[2] = String.valueOf(newSize);
        } catch (NumberFormatException e) {
            // ignore
            return;
        }
        PreferencesData.set("editor.font", StringUtils.join(pieces, ','));
        getEditors().forEach(Editor::applyPreferences);
    }

    public List<Editor> getEditors() {
        return new LinkedList<>(editors);
    }

    public void handleClose(Editor editor) {
        if (editor.getExperiment().isExperimentRunning()) {
            JOptionPane.showMessageDialog(editor, "Щоб вийти, завершіть експеримент", "Експеримент запущено", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        editor.getExperimentController().exit();
        if (editors.size() == 1) {
            editor.setVisible(false);
            editors.remove(editor);
            System.exit(1);
        } else {
            editor.setVisible(false);
            editor.dispose();
            editors.remove(editor);
        }
    }

    public void handleNew() throws Exception {
        try {
            File file = BaseHelper.createNewUntitled();
            if (file != null) {
                handleOpen(file, true);
            }

        } catch (IOException ignored) { }
    }

    private void restoreExperiment() throws Exception {
        String path = PreferencesData.getNonEmpty("last.experiment.path", null);
        if (path == null) return;
        if (BaseInit.getExperimentsFolder() != null && !new File(path).isAbsolute()) {
            File absolute = new File(BaseInit.getExperimentsFolder(), path);
            try {
                path = absolute.getCanonicalPath();
            } catch (IOException ignored) { }
        }
        int[] location = BaseHelper.retrieveExperimentLocation();
        handleOpen(new File(path), location, false);
    }

    public void handleOpen(File file) throws Exception {
        handleOpen(file, false);
    }

    public void handleOpen(File file, boolean untitled) throws Exception {
        handleOpen(file, nextEditorLocation(), untitled);
    }

    public void handleOpen(File file, int[] location, boolean untitled) throws Exception {
        handleOpen(file,location, location, untitled);
    }

    private void handleOpen(File file, int[] storedLocation, int[] defaultLocation, boolean untitled) throws Exception {
        for (Editor editor : editors) {
            if (editor.getExperiment().getFile().equals(file)) {
                editor.toFront();
                return;
            }
        }
        Editor editor = new Editor(this, file, storedLocation, defaultLocation, BaseInit.getPlatform(), untitled);
        if (editor.getExperimentController() == null) {
            return;
        }
        editors.add(editor);
        SwingUtilities.invokeLater(() -> editor.setVisible(true));
    }

    public Editor getActiveEditor() {
        return activeEditor;
    }

    public void handleOpenPrompt() throws Exception  {
        FileDialog fd = new FileDialog(activeEditor, "Відкрити експеримент...", FileDialog.LOAD);
        File lastFolder = new File(PreferencesData.get("last.folder", BaseInit.getExperimentsFolder().getAbsolutePath()));
        if (lastFolder.exists() && lastFolder.isFile()) {
            lastFolder = lastFolder.getParentFile();
        }
        fd.setDirectory(lastFolder.getAbsolutePath());
        fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".fim"));
        fd.setVisible(true);
        String directory = fd.getDirectory();
        String filename = fd.getFile();
        if (filename == null) return;
        File inputFile = new File(directory, filename);
        PreferencesData.set("last.folder", inputFile.getAbsolutePath());
        handleOpen(inputFile);
    }

    protected boolean addExperiments(JMenu menu, File folder) {
        if (folder == null)
            return false;
        if (!folder.isDirectory()) return false;
        File[] files = folder.listFiles();
        if (files == null) return false;
        Arrays.sort(files, (file1, file2) -> file1.getName().compareToIgnoreCase(file2.getName()));
        boolean iFound = false;
        for (File subfolder : files) {
            if (!FileUtils.isSCCSOrHiddenFile(subfolder) && subfolder.isDirectory()
                    && addExperimentsSubmenu(menu, subfolder.getName(), subfolder)) {
                iFound = true;

            }
        }
        return iFound;
    }

    private boolean addExperimentsSubmenu(JMenu menu, String name, File folder) {
        ActionListener listener = e -> {
            String path = e.getActionCommand();
            File file = new File(path);
            if (file.exists()) {
                try {
                    handleOpen(file);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                showWarning("Експеримент не існує",
                         "Вибраного експерименту більше не існує.\n"
                                + "Можливо, вам знадобиться перезапустити StartFP100,\n"
                                + "щоб оновити меню експериментів.", null);
            }
        };

        File entry = new File(folder, name + ".fim");
        if (entry.exists()) {
            if (!BaseInit.isSanitaryName(name)) {
                    String complaining = "Експеримент " + name + " не можна використовувати.\n"
                                            + "Імена експериментів повинні містити лише літери та цифри\n"
                                            + "(лише ASCII символи без пробілів, і не може починатися з числа).\n"
                                            + "Щоб позбутися цього повідомлення, видаліть або перейменуйте експеремент\n"
                                             + entry.getAbsolutePath();
                    showMessage("Ігнорування експерименту з поганою назвою", complaining);
                return false;
            }
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(listener);
            item.setActionCommand(entry.getAbsolutePath());
            menu.add(item);
            return true;
        }
        JMenu submenu = new JMenu(name);
        boolean found = addExperiments(submenu, folder);
        if (found) {
            menu.add(submenu);
            MenuScroller.setScrollerFor(submenu);
        }
        return found;
    }

    public void rebuildRecentExperimentsMenuItems() {
        Set<File> recentExperiments = new LinkedHashSet<File>() {
            @Override
            public boolean add(File file) {
                if (size() >= base.helpers.BaseHelper.RECENT_EXPERIMENTS_MAX_SIZE) {
                    return false;
                }
                return super.add(file);
            }
        };
        for (String path : PreferencesData.getCollection("recent.experiments")) {
            File file = new File(path);
            if (file.exists()) {
                recentExperiments.add(file);
            }
        }
        recentExperimentsMenuItems.clear();
        for (final File recentExperiment : recentExperiments) {
            JMenuItem recentExperimentMenuItem = new JMenuItem(recentExperiment.getParentFile().getName());
            recentExperimentMenuItem.addActionListener(actionEvent -> {
                try {
                    handleOpen(recentExperiment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            recentExperimentsMenuItems.add(recentExperimentMenuItem);
        }
    }

    public List<JMenuItem> getRecentExperimentsMenuItems() {
        return recentExperimentsMenuItems;
    }

    protected int[] nextEditorLocation() {
        if (activeEditor == null) {
            return base.helpers.BaseHelper.defaultLocation();
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        synchronized (editors) {
            int[] location = activeEditor.getPlacement();
            final int OVER = 50;
            location[0] += OVER;
            location[1] += OVER;

            if (location[0] == OVER || location[2] == OVER
                    || location[0] + location[2] > screen.width
                    || location[1] + location[3] > screen.height) {
                int[] l = base.helpers.BaseHelper.defaultLocation();
                l[0] *= Math.random() * 2;
                l[1] *= Math.random() * 2;
                return l;
            }
            return location;
        }
    }

    public void handleActivated(Editor editor) {
        activeEditor = editor;
        activeEditor.rebuildRecentExperimentsMenu();
        EditorConsole.setCurrentEditorConsole(editor.console);
    }

    public void handlePrefs() {
        Settings settings = new Settings(activeEditor);
        settings.setLocationRelativeTo(activeEditor);
        settings.setVisible(true);
    }

    public void handleQuit() {
        if (!PreferencesData.getBoolean("runtime.experiment.running", false)) {
            for (Editor editor : editors) {
                editor.getExperimentController().exit();
            }
            System.exit(1);
        }
    }

    public void handleAbout() {
        System.out.println("handleAbout");
    }

    public void showHelp() {
        System.out.println("showHelp");
    }

    public void openURL(String url) {
        try {
            BaseInit.getPlatform().openURL(url);
        } catch (Exception e) {
            showWarning("Проблема відкриття URL", "Не вдалося відкрити URL-адресу\n" + url, e);
        }
    }

    public static DiscoveryManager getDiscoveryManager() {
        return BaseInit.getDiscoveryManager();
    }

    public void handleTestModulesConnection(Editor editor, boolean b) {
        if (PreferencesData.getBoolean("runtime.experiment.running", false)) {
            JOptionPane.showMessageDialog(editor, "Кесперимент запущено", "Повідомлення", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!PreferencesData.getBoolean("runtime.valid.modules", false)) {
            editor.statusNotice("Перейдіть у Інструменти -> Налаштування і оновіть список модулів");
            return;
        }
        if (!(PreferencesData.getBoolean("check.modules", false) || b)) return; // перевірка якщо виставлено прапорець
        editor.setEnabledItem(false);
        new Thread(() -> new CheckModules(editor)).start();
    }

    public void handleTestModulesConnection(Editor editor) {
        handleTestModulesConnection(editor, false);
    }

    public void handleDeviceInformation() {
        if (!PreferencesData.getBoolean("runtime.general.use.native.list.serial", false)) {
            activeEditor.statusError(nativeException);
            return;
        }
        String info = BaseInit.getPlatform().resolveDeviceAttachedToNative(PreferencesData.get("serial.port"));
        StringBuilder stringBuilder = new StringBuilder();
        String noInfo = "Інформація відсутня";
        if (info.length() > 4) {
            String[] str = info.split(" ");
            if (str.length != 1) {
                for(int i = 1; i < str.length; i++) {
                    if (str[i].equals("-")) {
                        stringBuilder.append("\n");
                        continue;
                    }
                    stringBuilder.append(str[i]);
                    stringBuilder.append(" ");
                }
                stringBuilder.append("\n");
            }
            if (str.length != 0) {
                stringBuilder.append(str[0]);
            } else {
                stringBuilder.append(noInfo);
            }
        } else {
            stringBuilder.append(noInfo);
        }
        JOptionPane.showMessageDialog(activeEditor, stringBuilder, "Інформація про пристрій", JOptionPane.INFORMATION_MESSAGE);
    }

    public void rebuildToolbarMenu(JMenu toolbarMenu) {
    }

    public void handleExport(Editor editor) {
        FileDialog fd = new FileDialog(editor, "Експортувати...", FileDialog.SAVE);
        fd.setDirectory(editor.getExperiment().getFolder().getParentFile().getAbsolutePath());
        fd.setFile(editor.getExperiment().getName() + ".xlsx");
        fd.setVisible(true);
        String newParentDir = fd.getDirectory();
        String newName = fd.getFile();
        if (newName == null) return;
        editor.statusNotice("Експортувати...");
        newName = checkName(editor, newName);
        File newFolder;
        if (newName.endsWith(".xlsx")) {
            newFolder = new File(newParentDir, newName);
        } else {
            newFolder = new File(newParentDir, newName + ".xlsx");
        }
        try {
            new ExportExcel(editor, newFolder);
        } catch (Exception e) {
            editor.statusError(e);
            return;
        }
        editor.statusNotice("Експеримент збережено");
    }
}