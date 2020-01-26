package base.platforms.linux;

import java.io.File;

public class Platform extends base.platforms.Platform {

    @Override
    public File getDefaultExperimentsFolder() throws Exception {
        File home = new File(System.getProperty("user.home"));
        return new File(home, "StartFP100");
    }

}
