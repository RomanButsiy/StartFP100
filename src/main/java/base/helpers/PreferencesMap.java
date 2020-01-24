package base.helpers;

import base.legacy.PApplet;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PreferencesMap extends LinkedHashMap<String, String> {

    public PreferencesMap() {
        super();
    }

    public PreferencesMap(Map<String, String> table) {
        super(table);
    }

    public void putBoolean(String key, boolean value) {
        put(key, value ? "true" : "false");
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public void load(File file) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            load(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    public void load(InputStream input) throws IOException {
        String[] lines = PApplet.loadStrings(input);
        assert lines != null;
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '#')
                continue;
            int equals = line.indexOf('=');
            if (equals != -1) {
                String key = line.substring(0, equals).trim();
                String value = line.substring(equals + 1).trim();
                key = processPlatformSuffix(key, ".linux", OSUtils.isLinux());
                key = processPlatformSuffix(key, ".windows", OSUtils.isWindows());
                if (key != null)
                    put(key, value);
            }
        }
    }

    protected String processPlatformSuffix(String key, String suffix, boolean isCurrentPlatform) {
        if (key == null)
            return null;
        // Key does not end with the given suffix? Process as normal
        if (!key.endsWith(suffix))
            return key;
        // Not the current platform? Ignore this key
        if (!isCurrentPlatform)
            return null;
        // Strip the suffix from the key
        return key.substring(0, key.length() - suffix.length());
    }




}
