package base.platforms.windows;

import base.PreferencesData;
import base.legacy.PApplet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Platform extends base.platforms.Platform {

    private File settingsFolder;

    public void init() throws Exception {
        super.init();
        checkPath();
        recoverSettingsFolderPath();
    }

    @Override
    public void fixSettingsLocation() throws Exception {
        Path oldSettingsFolder = recoverOldSettingsFolderPath();
        if (!Files.exists(oldSettingsFolder)) return;
        if (!Files.exists(oldSettingsFolder.resolve(Paths.get("preferences.txt")))) return;
        if (settingsFolder.exists()) return;
        Files.move(oldSettingsFolder, settingsFolder.toPath());
    }

    @Override
    public File getDefaultExperimentsFolder() throws Exception {
        Path path = Win32KnownFolders.getDocumentsFolder().toPath();
        return path.resolve("StartFP100").toFile();
    }

    @Override
    public File getSettingsFolder() {
        return settingsFolder;
    }

    @Override
    public void fixPrefsFilePermissions(File prefsFile) throws IOException {
        //noop
    }

    private Path recoverOldSettingsFolderPath() throws Exception {
        Path path = Win32KnownFolders.getRoamingAppDataFolder().toPath();
        return path.resolve("StartFP100");
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

    private void recoverSettingsFolderPath() throws Exception {
        Path path = Win32KnownFolders.getLocalAppDataFolder().toPath();
        settingsFolder = path.resolve("StartFP100").toFile();
    }

    }
