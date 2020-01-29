package base.platforms.windows;

import base.legacy.PApplet;
import com.sun.jna.platform.win32.Shell32;

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
    public void openURL(String url) throws Exception {
        if (!url.startsWith("http") && !url.startsWith("file:")) {
            // Check if we are trying to open a local file
            File file = new File(url);
            if (file.exists()) {
                // in this case convert the path to a "file:" url
                url = file.toURI().toString();
            }
        }
        if (url.startsWith("http") || url.startsWith("file:")) {
            // this allows to open the file on Windows 10 that
            // has a more strict permission policy for cmd.exe
            final int SW_SHOW = 5;
            Shell32.INSTANCE.ShellExecute(null, null, url, null, null, SW_SHOW);
            return;
        }

        // this is not guaranteed to work, because who knows if the
        // path will always be c:\progra~1 et al. also if the user has
        // a different browser set as their default (which would
        // include me) it'd be annoying to be dropped into ie.
        //Runtime.getRuntime().exec("c:\\progra~1\\intern~1\\iexplore "
        // + currentDir

        // the following uses a shell execute to launch the .html file
        // note that under cygwin, the .html files have to be chmodded +x
        // after they're unpacked from the zip file. i don't know why,
        // and don't understand what this does in terms of windows
        // permissions. without the chmod, the command prompt says
        // "Access is denied" in both cygwin and the "dos" prompt.
        //Runtime.getRuntime().exec("cmd /c " + currentDir + "\\reference\\" +
        //                    referenceFile + ".html");

        // just launching the .html file via the shell works
        // but make sure to chmod +x the .html files first
        // also place quotes around it in case there's a space
        // in the user.dir part of the url
        Runtime.getRuntime().exec("cmd /c \"" + url + "\"");
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
