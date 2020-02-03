package base;

import java.io.File;
import base.helpers.*;
import base.platforms.Platform;
import base.serial.DiscoveryManager;

public class BaseInit {

    static Platform platform;
    static UserNotifier notifier = new BasicUserNotifier();
    static String currentDirectory = System.getProperty("user.dir");
    private static DiscoveryManager discoveryManager;

    static public File getDefaultExperimentsFolder() {
        File experimentsFolder = null;
        try {
            experimentsFolder = getPlatform().getDefaultExperimentsFolder();
        } catch (Exception ignored) { }
        return experimentsFolder;
    }

    static public String getExperimentsPath() {
        String experimentsPath = PreferencesData.get("experiments.path");
        if (experimentsPath != null) {
            File experimentsFolder = absoluteFile(experimentsPath);
            if (!experimentsFolder.exists()) {
                showWarning("Папка ескпериментів зникла",
                         "Папки з ескспериментами більше не існує.", null);
                experimentsPath = null;
            }
        }
        return experimentsPath;
    }

    static public File getExperimentsFolder() {
        return absoluteFile(PreferencesData.get("experiments.path"));
    }

    static public boolean isSanitaryName(String name) {
        return sanitizeName(name).equals(name);
    }

    public static File getSettingsFile(String filename) {
        return new File(getSettingsFolder(), filename);
    }

    static public File getContentFile(String name) {
        String appDir = System.getProperty("APP_DIR");
        if (appDir == null || appDir.length() == 0) {
            appDir = currentDirectory;
        }
        return new File(new File(appDir), name);
    }

    static public File absoluteFile(String path) {
        if (path == null) return null;
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(currentDirectory, path);
        }
        return file;
    }

    static public void initParameters(String[] args) throws Exception {
        String preferencesFile = null;
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--preferences-file")) {
                preferencesFile = args[i + 1];
                break;
            }
        }
        PreferencesData.init(absoluteFile(preferencesFile));
    }

    static public File getSettingsFolder() {
        File settingsFolder = null;
        String preferencesPath = PreferencesData.get("settings.path");
        if (preferencesPath != null) {
            settingsFolder = absoluteFile(preferencesPath);
        } else {
            try {
                settingsFolder = getPlatform().getSettingsFolder();
            } catch (Exception e) {
                showError("Проблема отримання папки налаштувань",
                          "Помилка отримання папки налаштувань StartFP100.", e);
            }
        }
        assert settingsFolder != null;
        if (!settingsFolder.exists()) {
            if (!settingsFolder.mkdirs()) {
                showError("Проблеми з налаштуваннями",
                          "StartFP100 не може запускатися, оскільки не може\n" +
                                    "створити папку для зберігання ваших налаштувань." , null);
            }
        }
        return settingsFolder;
    }

    static public Platform getPlatform() {
        return platform;
    }

    public static void initPlatform() {
        try {
            Class<?> platformClass = Class.forName("base.platforms.Platform");
            if (OSUtils.isWindows()) {
                platformClass = Class.forName("base.platforms.windows.Platform");
            } else if (OSUtils.isLinux()) {
                platformClass = Class.forName("base.platforms.linux.Platform");
            }
            platform = (Platform) platformClass.newInstance();
        } catch (Exception e) {
            showError("Проблема з налаштуванням платформи ",
                    "Під час завантаження сталася невідома помилка", e);
        }
    }

    static public String sanitizeName(String origName) {
        char[] c = origName.toCharArray();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < c.length; i++) {
            if (((c[i] >= '0') && (c[i] <= '9')) ||
                    ((c[i] >= 'a') && (c[i] <= 'z')) ||
                    ((c[i] >= 'A') && (c[i] <= 'Z')) ||
                    ((i > 0) && (c[i] == '-')) ||
                    ((i > 0) && (c[i] == '.'))) {
                buffer.append(c[i]);
            } else {
                buffer.append('_');
            }
        }
        if (buffer.length() > 63) {
            buffer.setLength(63);
        }
        return buffer.toString();
    }

    public static DiscoveryManager getDiscoveryManager() {
        if (discoveryManager == null) {
            discoveryManager = new DiscoveryManager();
        }
        return discoveryManager;
    }

    static public void showError(String title, String message, int exit_code) {
        showError(title, message, null, exit_code);
    }

    static public void showError(String title, String message, Throwable e) {
        notifier.showError(title, message, e, 1);
    }

    static public void showError(String title, String message, Throwable e, int exit_code) {
        notifier.showError(title, message, e, exit_code);
    }

    static public void showMessage(String title, String message) {
        notifier.showMessage(title, message);
    }

    static public void showWarning(String title, String message, Exception e) {
        notifier.showWarning(title, message, e);
    }

    static public void showWarning(String title, String message, Throwable e) {
        notifier.showWarning(title, message, e);
    }

    public static void selectSerialPort(String port) {
        PreferencesData.set("serial.port", port);
    }

    public static void selectSignalForm(int signal) {
        PreferencesData.setInteger("signal.form", signal);
    }

    public static void selectRate(String rate) {
        PreferencesData.set("serial.port.rate", rate);
    }

}
