package base.helpers;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private static final List<String> SOURCE_CONTROL_FOLDERS = Arrays.asList("CVS", "RCS", ".git", ".svn", ".hg", ".bzr");

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

    public static boolean isSCCSOrHiddenFile(File file) {
        return isSCCSFolder(file) || isHiddenFile(file);
    }

    public static boolean isSCCSFolder(File file) {
        return file.isDirectory() && SOURCE_CONTROL_FOLDERS.contains(file.getName());
    }

    public static boolean isHiddenFile(File file) {
        return file.isHidden() || file.getName().charAt(0) == '.';
    }

}
