package base.platforms.windows;

import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.ShlObj;
import static com.sun.jna.platform.win32.KnownFolders.FOLDERID_LocalAppData;
import static com.sun.jna.platform.win32.KnownFolders.FOLDERID_RoamingAppData;

import java.io.File;

public class Win32KnownFolders {

    public static File getLocalAppDataFolder() {
        try {
            return new File(Shell32Util.getKnownFolderPath(FOLDERID_LocalAppData));
        } catch (Throwable ignored) {}
        return new File(Shell32Util.getFolderPath(ShlObj.CSIDL_LOCAL_APPDATA));
    }

    public static File getRoamingAppDataFolder() {
        try {
            return new File(Shell32Util.getKnownFolderPath(FOLDERID_RoamingAppData));
        } catch (Throwable ignored) { }
        return new File(Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA));
    }

}


