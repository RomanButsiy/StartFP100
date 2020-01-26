package base;

import base.helpers.BaseHelper;

import java.io.File;
import java.util.Objects;

import static base.BaseInit.getContentFile;
import static base.PreferencesData.save;

public class Base {

    public Base(String[] args) throws Exception {
        BaseInit.initPlatform();
        BaseInit.getPlatform().init();
        String storeDirectory = Objects.requireNonNull(BaseInit.getSettingsFolder()).getAbsolutePath();
        System.out.println("Set store directory " + storeDirectory);
        BaseInit.initParameters(args);
        if (BaseInit.getExperimentsPath() == null) {
            File defaultFolder = BaseHelper.getDefaultExperimentsFolderOrPromptForIt();
            PreferencesData.set("experiments.path", defaultFolder.getAbsolutePath());
        }

        save();

        File portableFolder = getContentFile("portable");
        System.out.println(portableFolder.exists());
    }
}
