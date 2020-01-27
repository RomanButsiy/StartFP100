package base.helpers;

import java.awt.*;

public abstract class PreferencesHelper {

    public static Color parseColor(String v) {
        try {
            if (v.indexOf("#") == 0)
                v = v.substring(1);
            return new Color(Integer.parseInt(v, 16));
        } catch (Exception e) {
            return null;
        }
    }

    public static void putColor(PreferencesMap prefs, String attr, Color color) {
        prefs.put(attr, "#" + String.format("%06x", color.getRGB() & 0xffffff));
    }

    public static Font getFont(PreferencesMap prefs, String key) {
        String value = prefs.get(key);
        if (value == null)
            return null;
        String[] split = value.split(",");
        if (split.length != 3)
            return null;

        String name = split[0];
        int style = Font.PLAIN;
        if (split[1].contains("bold"))
            style |= Font.BOLD;
        if (split[1].contains("italic"))
            style |= Font.ITALIC;
        int size;
        try {
            // ParseDouble handle numbers with decimals too
            size = (int) Double.parseDouble(split[2]);
            if (size < 1) // Do not allow negative or zero size
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            // for wrong formatted size pick the default
            size = 12;
        }
        return new Font(name, style, size);
    }

}
