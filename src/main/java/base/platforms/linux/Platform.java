package base.platforms.linux;

import base.BaseInit;
import base.PreferencesData;

import javax.swing.*;
import java.io.File;

public class Platform extends base.platforms.Platform {

    @Override
    public boolean openFolderAvailable() {
        if (PreferencesData.get("launcher") != null) {
            return true;
        }

        // Attempt to use xdg-open
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "xdg-open" });
            p.waitFor();
            PreferencesData.set("launcher", "xdg-open");
            return true;
        } catch (Exception ignored) { }

        // Attempt to use gnome-open
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "gnome-open" });
            p.waitFor();
            // Not installed will throw an IOException (JDK 1.4.2, Ubuntu 7.04)
            PreferencesData.set("launcher", "gnome-open");
            return true;
        } catch (Exception ignored) { }

        // Attempt with kde-open
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "kde-open" });
            p.waitFor();
            PreferencesData.set("launcher", "kde-open");
            return true;
        } catch (Exception ignored) { }

        return false;
    }

    @Override
    public void setLookAndFeel() throws Exception {
        System.setProperty("sun.desktop", "gnome");
        super.setLookAndFeel();
    }

    @Override
    public File getDefaultExperimentsFolder() throws Exception {
        File home = new File(System.getProperty("user.home"));
        return new File(home, "StartFP100");
    }

    @Override
    public int getSystemDPI() {
        return UIManager.getFont("Menu.font").getSize() * 96 / 12;
    }

}
