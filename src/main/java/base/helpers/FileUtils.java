package base.helpers;

import java.io.File;

public class FileUtils {

    public static String addExtension(String filename, String extension) {
        return extension.equals("") ? filename : (filename + "." + extension);
    }

    public static SplitFile splitFilename(File file) {
        return splitFilename(file.getName());
    }

    public static SplitFile splitFilename(String filename) {
        int index = filename.lastIndexOf(".");

        if (index >= 0)
            return new SplitFile(filename.substring(0, index), filename.substring(index + 1));
        return new SplitFile(filename, "");
    }

    public static class SplitFile {
        public SplitFile(String basename, String extension) {
            this.basename = basename;
            this.extension = extension;
        }

        public String basename;
        public String extension;

        public String join() {
            return addExtension(basename, extension);
        }
    }

}
