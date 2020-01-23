package base.platforms.windows;

import base.legacy.PApplet;

import java.io.File;
import java.nio.file.Path;

public class Platform extends base.platforms.Platform {

    private File settingsFolder;

    public void init() {
        super.init();
        checkPath();
        recoverSettingsFolderPath();
    }

    private void checkPath() {
        String path = System.getProperty("java.library.path");
        String[] pieces = PApplet.split(path, File.pathSeparatorChar);
        String[] legit = new String[pieces.length];
        int legitCount = 0;
        for (String item : pieces) {
            if (item.startsWith("\"")) {
                item = item.substring(1);
            }
            if (item.endsWith("\"")) {
                item = item.substring(0, item.length() - 1);
            }
            if (item.endsWith(File.separator)) {
                item = item.substring(0, item.length() - File.separator.length());
            }
            File directory = new File(item);
            if (!directory.exists()) {
                continue;
            }
            if (item.trim().length() == 0) {
                continue;
            }
            legit[legitCount++] = item;
        }
        legit = PApplet.subset(legit, 0, legitCount);
        String newPath = PApplet.join(legit, File.pathSeparator);
        if (!newPath.equals(path)) {
            System.setProperty("java.library.path", newPath);
        }
    }

    private void recoverSettingsFolderPath() {
        Path path = Win32KnownFolders.getLocalAppDataFolder().toPath();
        settingsFolder = path.resolve("StartFP100").toFile();
    }

    }
