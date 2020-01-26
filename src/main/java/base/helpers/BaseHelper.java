package base.helpers;

import base.BaseInit;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static base.BaseInit.showError;

public class BaseHelper {

    public static File getDefaultExperimentsFolderOrPromptForIt() {
        boolean result = true;
        File experimentsFolder = BaseInit.getDefaultExperimentsFolder();
        if (experimentsFolder == null) {
            experimentsFolder = promptExperimentsLocation();
            result = experimentsFolder != null;
        }
        if (result) {
            if (!experimentsFolder.exists()) {
                result = experimentsFolder.mkdirs();
            }
        }
        if (!result) {
            showError(null, "StartFP100 не може запуститися, оскільки він не може\n" +
                                          "створити папку для зберігання ваших експериментів.", null);
        }
        return experimentsFolder;
    }

    static protected File promptExperimentsLocation() {
        String prompt = "Виберіть (або створіть нову) папку для експериментів...";
        return selectFolder(prompt, null, null);
    }

    static public File selectFolder(String prompt, File folder, Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(prompt);
        if (folder != null) {
            fc.setSelectedFile(folder);
        }
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returned = fc.showOpenDialog(parent);
        if (returned == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

}
