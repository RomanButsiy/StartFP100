package base.helpers;

import base.BaseInit;
import base.Editor;
import base.PreferencesData;
import base.legacy.PApplet;
import jssc.SerialPortException;
import org.apache.commons.compress.utils.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static base.BaseInit.*;

public class BaseHelper {

    public static final int RECENT_EXPERIMENTS_MAX_SIZE = 3;
    static public String[] months = {
            "jan", "feb", "mar", "apr", "may", "jun",
            "jul", "aug", "sep", "oct", "nov", "dec"
    };


    static public void copyFile(File sourceFile, File targetFile) throws IOException {
        InputStream from = null;
        OutputStream to = null;
        try {
            from = new BufferedInputStream(new FileInputStream(sourceFile));
            to = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buffer = new byte[16 * 1024];
            int bytesRead;
            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead);
            }
            to.flush();
        } finally {
            IOUtils.closeQuietly(from);
            IOUtils.closeQuietly(to);
        }
        targetFile.setLastModified(sourceFile.lastModified());
    }


    public static File createNewUntitled() throws IOException {
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
        newExperimentFolder.mkdirs();
         File newExperimentFile = new File(newExperimentFolder, newExperimentName + ".fim");
        if (!newExperimentFile.createNewFile()) {
            throw new IOException();
        }
        return newExperimentFile;
    }

    public static void parsePortException(Editor editor, SerialPortException e) {
        if (e.toString().contains("Port busy")) {
            editor.statusError("Порт зайнятий");
            editor.statusNotice("Закрийте програми, які можуть використовувати порт");
            return;
        }
        if (e.toString().contains("Port not found")) {
            editor.statusError("Пристрій не підключено");
            return;
        }
        editor.statusError(e);
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

    public static void LittleBitPreferencesModuleTest(Editor activeEditor) {
        int numberOfModules = PreferencesData.getInteger("number.of.modules", 0);
        List<String> IdModules = new ArrayList<>();
        if (numberOfModules <= 0) {
            activeEditor.statusError("Список модулів пустий");
            activeEditor.statusNotice("Перейдіть у Інструменти -> Налаштування і обновіть його");
            PreferencesData.setBoolean("runtime.valid.modules", false);
            return;
        }
        String dacModule = PreferencesData.get("type.dac.module");
        if (dacModule == null) {
            activeEditor.statusError("Тип цифро-аналогового перетворювача не вказано");
            PreferencesData.setBoolean("runtime.valid.modules", false);
            return;
        }
        String IdDacModule = PreferencesData.get("module." + dacModule + ".id");
        if (IdDacModule == null) {
            activeEditor.statusError("Id цифро-аналогового перетворювача не вказано");
            PreferencesData.setBoolean("runtime.valid.modules", false);
            return;
        }
        boolean flag = false;
        for (int i = 0; i < numberOfModules; i++) {
            String id = PreferencesData.get("module." + i +".id");
            if (id == null) continue;
            if (id.equals(IdDacModule)) {
                flag = true;
                continue;
            }
            IdModules.add(id);
        }
        if (!flag) {
            activeEditor.statusError("Id цифро-аналогового перетворювача вказано невірно");
            PreferencesData.setBoolean("runtime.valid.modules", false);
            return;
        }
        PreferencesData.set("runtime.dac.module", IdDacModule);
        PreferencesData.setCollection("runtime.Id.modules", IdModules);
        PreferencesData.setBoolean("runtime.valid.modules", true);
    }

    public static boolean recheckModules(Editor editor) {

        return true;
    }

}
