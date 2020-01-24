package base.platforms;

import java.io.File;
import java.io.IOException;

public class Platform {

    public void init() throws Exception {
    }

    public void fixPrefsFilePermissions(File prefsFile) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[]{"chmod", "600", prefsFile.getAbsolutePath()}, null, null);
        process.waitFor();
    }

    public File getSettingsFolder() throws Exception {
        File home = new File(System.getProperty("user.home"));
        return new File(home, ".startFP100");
    }

    public void fixSettingsLocation() throws Exception {
        //noop
    }

}
