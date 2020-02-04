package base.helpers;

import base.Editor;
import base.PreferencesData;

public class SendOne {

    public SendOne(Editor editor, String result, String id) {
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate", "115200");
        if (port == null) {
            editor.statusError("Порт не вибрано");
            return;
        }
        editor.statusNotice("Надсилання команди на...");
    }
}
