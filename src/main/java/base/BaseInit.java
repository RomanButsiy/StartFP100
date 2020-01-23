package base;

import java.io.File;
import base.helpers.*;
import base.platforms.Platform;

public class BaseInit {

    static File portableFolder = null;
    static Platform platform;
    static UserNotifier notifier = new BasicUserNotifier();

    public static File getSettingsFile(String filename) {
        return new File(getSettingsFolder(), filename);
    }

    static public File getPortableFolder() {
        return portableFolder;
    }

    static public File getSettingsFolder() {
        if (getPortableFolder() != null)
            return getPortableFolder();

        File settingsFolder = null;

        return settingsFolder;
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
