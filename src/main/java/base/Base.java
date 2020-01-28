package base;

import base.helpers.BaseHelper;
import libraries.Theme;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Base {

    List<Editor> editors = Collections.synchronizedList(new ArrayList<Editor>());
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
        handleOpen(file, base.helpers.BaseHelper.defaultLocation(), untitled);
    }

    private void handleOpen(File file, int[] location, boolean untitled) throws Exception {
        for (Editor editor : editors) {
            if (editor.getExperiment().getFile().equals(file)) {
                editor.toFront();
                return;
            }
        }
        Editor editor = new Editor(this, file, location, BaseInit.getPlatform());
        if (editor.getExperimentController() == null) {
            return;
        }
        editor.untitled = untitled;
        editors.add(editor);
        SwingUtilities.invokeLater(() -> editor.setVisible(true));
        // add some code...

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

}
