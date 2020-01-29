package base;

import base.helpers.BaseHelper;
import libraries.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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

    public void handleOpenPrompt() {
        System.out.println("Open...");
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
    }
}
