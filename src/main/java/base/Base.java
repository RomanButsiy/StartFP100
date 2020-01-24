package base;

import java.io.File;
import java.util.Objects;

import static base.BaseInit.getContentFile;

public class Base {

    public Base(String[] args) throws Exception {
        BaseInit.initPlatform();
        BaseInit.getPlatform().init();
        System.out.println("Set store directory " + Objects.requireNonNull(BaseInit.getSettingsFolder()).getAbsolutePath());
        BaseInit.initParameters(args);


        File portableFolder = getContentFile("portable");
        System.out.println(portableFolder.exists());
    }
}
