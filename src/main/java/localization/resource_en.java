package localization;

import java.util.ListResourceBundle;

public class resource_en extends ListResourceBundle {
    protected Object[][] getContents() {
        return new Object[][] {
                {"OkKey", "Ok"},
                {"CancelKey", "Cancel"},
                {"titleFrame", "StartFP100"},
                {"menuFile", "File"},
                {"menuOpenFile", "Open file"},
                {"menuExit", "Exit"},
                {"menuTitleTools", "Tools"},
                {"menuSpeed", "Speed: "},
                {"menuPort", "Port: "},
                {"sendButton", "Send"},
                {"clearButton", "Clear output"},
                {"popupCut", "Cut"},
                {"popupCopy", "Copy"},
                {"popupPaste", "Paste"},
                {"menuSettings", "Settings"}
        };
    }
}
