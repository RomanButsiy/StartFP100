package base;

import java.io.File;
import base.helpers.*;
import base.platforms.Platform;

public class BaseInit {

    static Platform platform;
    static UserNotifier notifier = new BasicUserNotifier();
    static String currentDirectory = System.getProperty("user.dir");

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

}
