package base.platforms;

import base.BaseInit;
import base.PreferencesData;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Platform {

    public void init() throws Exception {
    }

    public void setLookAndFeel() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    public void openFolder(File file) throws Exception {
        String launcher = PreferencesData.get("launcher");
        if (launcher != null) {
            try {
                String folder = file.getAbsolutePath();
                Runtime.getRuntime().exec(new String[]{launcher, folder});
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showLauncherWarning();
        }
    }

    public void openURL(String url) throws Exception {
        String launcher = PreferencesData.get("launcher");
        if (launcher != null) {
            Runtime.getRuntime().exec(new String[]{launcher, url});
        } else {
            showLauncherWarning();
        }
    }


    public boolean openFolderAvailable() {
        return PreferencesData.get("launcher") != null;
    }

    protected void showLauncherWarning() {
        BaseInit.showWarning("Немає завантажувача",
                "Не вказано платформу, завантажувач недоступний.\n" +
                         "Щоб дозволити відкривання посилань або тек, додайте \n" +
                         "\"launcher=/path/to/app\" у файл preferences.txt", null);
    }

    public int getSystemDPI() {
        return 96;
    }

    public void fixPrefsFilePermissions(File prefsFile) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[]{"chmod", "600", prefsFile.getAbsolutePath()}, null, null);
        process.waitFor();
    }

    public File getSettingsFolder() throws Exception {
        File home = new File(System.getProperty("user.home"));
        return new File(home, ".startFP100");
    }

    public void fixSettingsLocation() throws Exception {
        //noop
    }

    public File getDefaultExperimentsFolder() throws Exception {
        return null;
    }

}
