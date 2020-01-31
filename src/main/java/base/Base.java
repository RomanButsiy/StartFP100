package base;

import base.helpers.BaseHelper;
import base.helpers.FileUtils;
import base.processing.Experiment;
import base.serial.DiscoveryManager;
import libraries.MenuScroller;
import libraries.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static base.BaseInit.*;

public class Base {

    final List<Editor> editors = Collections.synchronizedList(new ArrayList<Editor>());
    Editor activeEditor;
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
        restoreExperiment(); // fix me!
        if (editors.isEmpty()) handleNew();




        //save();
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

    public void handleClose(Editor editor) {
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
        String path = PreferencesData.get("last.experiment.path");
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
    }

    public void handlePrefs() {
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

    public void handleTestConnection() {
        System.out.println();
    }

    public void handleDeviceInformation() {

    }

    public void rebuildToolbarMenu(JMenu toolbarMenu) {
    }
}
