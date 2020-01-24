package base.platforms;

import java.io.File;

public class Platform {

    public void init() throws Exception {
    }

    public File getSettingsFolder() throws Exception {
        File home = new File(System.getProperty("user.home"));
        return new File(home, ".startFP100");
    }

}
