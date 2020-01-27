package base;

import base.helpers.BaseHelper;
import libraries.Theme;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static base.helpers.BaseHelper.defaultLocation;

public class Base {

    Editor editor;

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
        if (editor == null) handleNew();




        //save();
    }

    public void handleClose(Editor editor) {
        editor.setVisible(false);
        System.exit(1);
    }

    public void handleNew() throws Exception {
        try {
            File file = BaseHelper.getNewUntitled();
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

    public void handleOpen(File file, boolean untitled) throws Exception {
        handleOpen(file, defaultLocation(), untitled);
    }

    private void handleOpen(File file, int[] location, boolean untitled) throws Exception {
        editor = new Editor(this, file, location, BaseInit.getPlatform());
        editor.untitled = untitled;
        // add some code...

    }


    public void handleOpenPrompt() {
        System.out.println("Open...");
    }
}
