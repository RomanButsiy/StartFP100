package base.helpers;

public class OSUtils {

    static public boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    static public boolean isLinux() {
        return System.getProperty("os.name").contains("Linux");
    }

    static public String version() {
        return System.getProperty("os.version");
    }

}
