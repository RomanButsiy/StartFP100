import base.Base;
import base.helpers.OSUtils;

public class StartFP100 {

    // sudo usermod -aG dialout roman

    public static void main(String[] args) {
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
