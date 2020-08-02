package localization.languages;

import static localization.Translate.tr;

public class Languages {

    public static final Language[] languages;

    public static boolean have(String isoCode) {
        for (Language language : languages) {
            if (language.getIsoCode().equals(isoCode)) {
                return true;
            }
        }
        return false;
    }

    static {
        languages = new Language[] {
                new Language(tr("System Default"), "", ""),
                new Language(tr("English"), "English", "en"),
                new Language(tr("Ukrainian"), "Українська", "uk")
        };
    }

}
