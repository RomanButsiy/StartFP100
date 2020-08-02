package localization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Translate {

    private static ResourceBundle translate;

    public static void init(String language) throws MissingResourceException {
        String[] languageParts = language.split("_");
        Locale locale = Locale.getDefault();
        if (languageParts.length == 2) {
            locale = new Locale(languageParts[0], languageParts[1]);
        } else if (languageParts.length == 1 && !"".equals(languageParts[0])) {
            locale = new Locale(languageParts[0]);
        }
        Locale.setDefault(locale);
        translate = ResourceBundle.getBundle("localization.resources.Resource", Locale.getDefault());
    }

    public static String tr(String s) {
        String res;
        try {
            if (translate == null)
                res = s;
            else
                res = translate.getString(s);
        } catch (MissingResourceException e) {
            e.printStackTrace();
            System.out.println("MissingResourceException: " + s);
            res = s;
        }
        res = res.replace("%%", "%");
        return res;
    }

    public static String format(String fmt, Object... args) {
        fmt = fmt.replace("''", "'");
        fmt = fmt.replace("'", "''");
        return MessageFormat.format(fmt, args);
    }

}
