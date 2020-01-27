package base.helpers;

import base.BaseInit;
import base.Editor;
import base.PreferencesData;
import base.legacy.PApplet;
import com.sun.jna.platform.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static base.BaseInit.*;

public class BaseHelper {

    static public String[] months = {
            "jan", "feb", "mar", "apr", "may", "jun",
            "jul", "aug", "sep", "oct", "nov", "dec"
    };

    public static File getNewUntitled() throws IOException {
        File experimentsFolder = BaseInit.getExperimentsFolder();
        File newExperimentFolder;
        String newExperimentName;
        int index = 0;
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);  // 1..31
        int month = cal.get(Calendar.MONTH);  // 0..11
        String purity = months[month] + PApplet.nf(day, 2);
        do {
            if (index == 676) {
                showWarning("Час перерви",
                         "Ви досягли межі для автоматичного іменування нових експериментів\n" +
                                  "протягом дня. Як щодо того, щоб піти на прогулянку?", null);
                return null;
            }
            int multiples = index / 26;
            if (multiples > 0) {
                newExperimentName = ((char) ('a' + (multiples-1))) + "" + ((char) ('a' + (index % 26))) + "";
            }else{
                newExperimentName = ((char) ('a' + index)) + "";
            }
            newExperimentName = "Experiment_" + purity + newExperimentName;
            newExperimentFolder = new File(experimentsFolder, newExperimentName);
            index++;
        } while (newExperimentFolder.exists() || new File(experimentsFolder, newExperimentName).exists());
        File newExperimentFile = new File(newExperimentFolder, newExperimentName + ".fim");
        if (!newExperimentFolder.mkdirs()) return null;
        if (!newExperimentFile.createNewFile()) return null;
        return newExperimentFile;
    }

    public static int[] retrieveExperimentLocation() {
        if (PreferencesData.get("last.screen.height") == null) return defaultLocation();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int screenW = PreferencesData.getInteger("last.screen.width");
        int screenH = PreferencesData.getInteger("last.screen.height");

        if ((screen.width != screenW) || (screen.height != screenH)) return defaultLocation();

        String location = PreferencesData.get("last.experiment.location");
        if (location == null) return defaultLocation();
        return PApplet.parseInt(PApplet.split(location, ','));
    }

    public static int[] defaultLocation() {
        int defaultWidth = PreferencesData.getInteger("window.size.width.default");
        int defaultHeight = PreferencesData.getInteger("window.size.width.default");
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        return new int[] {
                (screen.width - defaultWidth) / 2,
                (screen.height - defaultHeight) / 2,
                defaultWidth, defaultHeight, 0
        };
    }

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
