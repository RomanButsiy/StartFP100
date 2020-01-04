package localization;

import java.util.ListResourceBundle;

public class resource_uk extends ListResourceBundle {
    protected Object[][] getContents() {
        return new Object[][] {
                {"OkKey", "Ok"},
                {"CancelKey", "Cancel"},
                {"titleFrame", "StartFP100"},
                {"menuFile", "Файл"},
                {"menuOpenFile", "Відкрити файл"},
                {"menuExit", "Вихід"},
                {"menuTitleTools", "Інструменти"},
                {"menuSpeed", "Швидуість: "},
                {"menuPort", "Порт: "},
                {"sendButton", "Надіслати"},
                {"clearButton", "Очистити вивід"},
                {"popupCut", "Вирізати"},
                {"popupCopy", "Копіювати"},
                {"popupPaste", "Вставити"}
        };
    }
}
