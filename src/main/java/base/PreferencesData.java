package base;

import java.io.File;

public class PreferencesData {

    private static final String PREFS_FILE = "preferences.txt";
    static File preferencesFile;

    static public void init(File file) {
        if (file != null) {
            preferencesFile = file;
        } else {
            preferencesFile = BaseInit.getSettingsFile(PREFS_FILE);
        }
    }

}
