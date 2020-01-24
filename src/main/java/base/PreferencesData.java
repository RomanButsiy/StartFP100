package base;

import base.helpers.PreferencesHelper;
import base.helpers.PreferencesMap;
import base.legacy.PApplet;
import base.legacy.PConstants;
import org.apache.commons.compress.utils.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class PreferencesData {

    private static final String PREFS_FILE = "preferences.txt";
    static PreferencesMap prefs = new PreferencesMap();
    static PreferencesMap defaults;
    static File preferencesFile;
    static boolean doSave = true;

    static public void init(File file) throws Exception {
        if (file == null) {
            BaseInit.getPlatform().fixSettingsLocation();
        }
        if (file != null) {
            preferencesFile = file;
        } else {
            preferencesFile = BaseInit.getSettingsFile(PREFS_FILE);
        }
        try {
            BaseInit.getPlatform().fixPrefsFilePermissions(preferencesFile);
        } catch (Exception ignored) {}
        try {
            prefs.load(new File(BaseInit.getContentFile("lib"), PREFS_FILE));
        } catch (IOException e) {
            BaseInit.showError(null, "Не вдалося прочитати налаштування за замовчуванням.\n" +
                                                    "Вам потрібно буде перевстановити StartFP100.", e);
        }
        defaults = new PreferencesMap(prefs);
        if (preferencesFile.exists()) {
            try {
                prefs.load(preferencesFile);
            } catch (IOException ex) {
                BaseInit.showError("Помилка читання налаштувань",
                                "Помилка читання файлу налаштувань. Видаліть (або перемістіть)\n"
                                        + preferencesFile.getAbsolutePath() + "і перезапустіть StartFP100.", ex);
            }
        }
        set("runtime.os", PConstants.platformNames[PApplet.platform]);

    }

    static protected void save() {
        if (!doSave) return;
        if (getBoolean("preferences.readonly")) return;
        // on startup, don't worry about it
        // this is trying to update the prefs for who is open
        // before Preferences.init() has been called.
        if (preferencesFile == null) return;

        PrintWriter writer = null;
        try {
            writer = PApplet.createWriter(preferencesFile);
            String[] keys = prefs.keySet().toArray(new String[0]);
            Arrays.sort(keys);
            for (String key : keys) {
                if (key.startsWith("runtime."))
                    continue;
                writer.println(key + "=" + prefs.get(key));
            }
            writer.flush();
        } catch (Throwable e) {
            BaseInit.showWarning(null, "", e);
            System.err.println("Не вдалося записати файл налаштувань: " + e.getMessage());
            return;
        } finally {
            IOUtils.closeQuietly(writer);
        }
        try {
            BaseInit.getPlatform().fixPrefsFilePermissions(preferencesFile);
        } catch (Exception ignored) { }
    }

    // .................................................................

    static public String get(String attribute) {
        return prefs.get(attribute);
    }

    static public String get(String attribute, String defaultValue) {
        String value = get(attribute);
        return (value == null) ? defaultValue : value;
    }

    static public String getNonEmpty(String attribute, String defaultValue) {
        String value = get(attribute, defaultValue);
        return ("".equals(value)) ? defaultValue : value;
    }

    public static boolean has(String key) {
        return prefs.containsKey(key);
    }

    public static void remove(String key) {
        prefs.remove(key);
    }

    static public String getDefault(String attribute) {
        return defaults.get(attribute);
    }


    static public void set(String attribute, String value) {
        prefs.put(attribute, value);
    }


    static public void unset(String attribute) {
        prefs.remove(attribute);
    }

    static public boolean getBoolean(String attribute, boolean defaultValue) {
        if (has(attribute)) {
            return getBoolean(attribute);
        }

        return defaultValue;
    }

    static public boolean getBoolean(String attribute) {
        return prefs.getBoolean(attribute);
    }


    static public void setBoolean(String attribute, boolean value) {
        prefs.putBoolean(attribute, value);
    }


    static public int getInteger(String attribute) {
        return Integer.parseInt(get(attribute));
    }

    static public int getInteger(String attribute, int defaultValue) {
        if (has(attribute)) {
            return getInteger(attribute);
        }

        return defaultValue;
    }

    static public void setInteger(String key, int value) {
        set(key, String.valueOf(value));
    }

    static public float getFloat(String attribute, float defaultValue) {
        if (has(attribute)) {
            return getFloat(attribute);
        }

        return defaultValue;
    }

    static public float getFloat(String attribute) {
        return Float.parseFloat(get(attribute));
    }

    static public PreferencesMap getMap() {
        return new PreferencesMap(prefs);
    }

    static public void removeAllKeysWithPrefix(String prefix) {
        prefs.keySet().removeIf(s -> s.startsWith(prefix));
    }

    static public void setDoSave(boolean value) {
        doSave = value;
    }

    static public Font getFont(String attr) {
        Font font = PreferencesHelper.getFont(prefs, attr);
        if (font == null) {
            String value = defaults.get(attr);
            prefs.put(attr, value);
            font = PreferencesHelper.getFont(prefs, attr);
        }
        return font;
    }

    public static Collection<String> getCollection(String key) {
        return Arrays.stream(get(key, "").split(","))
                // Remove empty strings from the collection
                .filter((v) -> !v.trim().isEmpty())
                .collect(Collectors.toList());
    }

    public static void setCollection(String key, Collection<String> values) {
        String value = String.join(",", values);
        set(key, value);
    }

}
