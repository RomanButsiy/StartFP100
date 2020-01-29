package base.platforms.windows;

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
    public boolean openFolderAvailable() {
        return true;
    }

    @Override
    public void openFolder(File file) throws Exception {
        String folder = file.getAbsolutePath();

        // doesn't work
        //Runtime.getRuntime().exec("cmd /c \"" + folder + "\"");

        // works fine on winxp, prolly win2k as well
        Runtime.getRuntime().exec("explorer \"" + folder + "\"");

        // not tested
        //Runtime.getRuntime().exec("start explorer \"" + folder + "\"");
    }

    @Override
    public int getSystemDPI() {
        int detected = detectSystemDPI();
        if (detected == -1)
            return super.getSystemDPI();
        return detected;
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

    public static int detectSystemDPI() {
        try {
            ExtUser32.INSTANCE.SetProcessDpiAwareness(ExtUser32.DPI_AWARENESS_SYSTEM_AWARE);
        } catch (Throwable ignored) { }
        try {
            ExtUser32.INSTANCE.SetThreadDpiAwarenessContext(ExtUser32.DPI_AWARENESS_CONTEXT_SYSTEM_AWARE);
        } catch (Throwable ignored) { }
        try {
            return ExtUser32.INSTANCE.GetDpiForSystem();
        } catch (Throwable e) {
            return -1;
        }
    }

    }
