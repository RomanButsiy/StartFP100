import java.util.Locale;
import java.util.ResourceBundle;

import base.Base;
import base.helpers.OSUtils;

public class StartFP100 {

    // sudo usermod -aG dialout roman

    private static final String[][] resourceBundle = {{"English", "Українська"}, {"en", "uk"}};
    private static final int selectedLanguage = 1;

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("localization.resource", new Locale(resourceBundle[1][selectedLanguage]));
        if (!OSUtils.isWindows()) {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        }
        System.setProperty("java.net.useSystemProxies", "true");
        try {
            new Base(args);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(255);
        }
    }
}
