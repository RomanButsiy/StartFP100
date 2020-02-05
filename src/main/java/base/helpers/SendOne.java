package base.helpers;

import SerialDriver.SerialDriver;
import base.Editor;
import base.PreferencesData;

import static base.helpers.BaseHelper.parsePortException;

public class SendOne {

    private StringBuffer serialBuffer;
    private String result = null;

    public SendOne(Editor editor, String command) {
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate", "115200");
        if (port == null) {
            editor.statusError("Порт не вибрано");
            return;
        }
        editor.statusNotice("Надсилання команди...");
        SerialDriver serialDriver;
        serialBuffer = new StringBuffer();
        try {
            serialDriver = new SerialDriver(port, rate, this::dataReadAction);
        } catch (Exception e) {
            parsePortException(editor, e);
            return;
        }
        final int responseTimeout = PreferencesData.getInteger("response.timeout", 200);
        serialBuffer.setLength(0);
        long startTime = System.currentTimeMillis();
        try {
            serialDriver.write(command);
        } catch (Exception e) {
            editor.statusError(e);
            return;
        }
        while ((System.currentTimeMillis() - startTime < responseTimeout)) {
            if (serialBuffer.indexOf("\r") != -1) {
                this.result = serialBuffer.toString();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
               editor.statusError(e);
               break;
            }
        }
        serialDriver.dispose();
    }

    public String getResult() {
        return result;
    }

    private void dataReadAction(String s) {
        serialBuffer.append(s);
    }

}
