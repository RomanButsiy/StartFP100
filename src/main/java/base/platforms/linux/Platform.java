package base.platforms.linux;

import javax.swing.*;
import java.io.File;

public class Platform extends base.platforms.Platform {

    @Override
    public void setLookAndFeel() throws Exception {
        System.setProperty("sun.desktop", "gnome");
        super.setLookAndFeel();
    }

    @Override
    public File getDefaultExperimentsFolder() throws Exception {
        File home = new File(System.getProperty("user.home"));
        return new File(home, "StartFP100");
    }

    @Override
    public int getSystemDPI() {
        return UIManager.getFont("Menu.font").getSize() * 96 / 12;
    }

}
