package base.helpers;

import java.io.File;

public class FileUtils {

    public static void recursiveDelete(File file) {
        if (!file.toString().contains("StartFP100")) return; // Just in case when debugging
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File current : files) {
                recursiveDelete(current);
            }
        }
        file.delete();
    }
}
