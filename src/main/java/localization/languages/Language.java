package localization.languages;

public class Language {

    private final String name;
    private final String originalName;
    private final String isoCode;

    public Language(String name, String originalName, String isoCode) {
        this.name = name;
        this.originalName = originalName;
        this.isoCode = isoCode;
    }

    @Override
    public String toString() {
        if (originalName.length() == 0) {
            return name;
        }
        return originalName + " (" + name + ")";
    }

    public String getIsoCode() {
        return isoCode;
    }

}