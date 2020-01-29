package base.processing;


import base.PreferencesData;

import java.io.File;
import java.io.IOException;

public class Experiment {

    private File folder;
    private File file;
    private boolean isUntitledAndNotSaved = false;

    public Experiment(File file) throws IOException {
        this.folder = file.getParentFile();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public File getFolder() {
        return folder;
    }


    static public File checkExperimentFile(File file) {
        String fileName = file.getName();
        File parent = file.getParentFile();
        String parentName = parent.getName();
        String fimName = parentName + ".fim";
        File altFimFile = new File(parent, fimName);

        if (fimName.equals(fileName))
            return file;

        if (altFimFile.exists())
            return altFimFile;

        return null;
    }

    public boolean isUntitledAndNotSaved() {
        return isUntitledAndNotSaved;
    }

    public void setUntitledAndNotSaved(boolean untitledAndNotSaved) {
        isUntitledAndNotSaved = untitledAndNotSaved;
    }

    public boolean isExperimentRunning() {
        return PreferencesData.getBoolean("runtime.experiment.running", false);
    }

    public void setExperimentRunning(boolean experimentRunning) {
        PreferencesData.setBoolean("runtime.experiment.running", experimentRunning);
    }
}
