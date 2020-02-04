package base.helpers;

import SerialDriver.SerialDriver;
import base.Editor;
import base.PreferencesData;
import jssc.SerialPortException;
import libraries.I7000;

import java.util.List;

public class CheckModules {
    private final Editor editor;
    private SerialDriver serialDriver;
    private StringBuffer serialBuffer;

    public CheckModules(Editor editor) {
        this.editor = editor;
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate", "115200");
        if (port == null) {
            editor.statusError("Порт не вибрано");
            return;
        }
        editor.statusNotice("Перевірка модулів FP100...");
        editor.statusNotice("Порт: " + port + " | Швидкість: " + rate);
        SerialDriver serialDriver;
        serialBuffer = new StringBuffer();
        try {
            serialDriver = new SerialDriver(port, rate, this::dataReadAction);
        } catch (SerialPortException e) {
            if (e.toString().contains("Port busy")) {
                editor.statusError("Порт зайнятий");
                editor.statusNotice("Закрийте програми, які можуть використовувати порт");
                return;
            }
            if (e.toString().contains("Port not found")) {
                editor.statusError("Пристрій не підключено");
                return;
            }
            editor.statusError(e);
            return;
        }
        this.serialDriver = serialDriver;
        List<String> modules = (List<String>) PreferencesData.getCollection("runtime.Id.modules");
        String dacModule = PreferencesData.get("runtime.dac.module");
        int responseTimeout = PreferencesData.getInteger("response.timeout", 200);
        for (String module : modules) {
            SearchDevices(module, responseTimeout);
        }
        PreferencesData.setBoolean("runtime.dac.module.ready", SearchDevices(dacModule, responseTimeout));
        serialDriver.dispose();
    }

    private boolean SearchDevices(String module, int responseTimeout) {
        serialBuffer.setLength(0);
        try {
            serialDriver.write(I7000.getModuleName(module));
            Thread.sleep(responseTimeout);
            if (serialBuffer.indexOf("\r") == -1) {
                editor.statusError("Модуль: " + module + " -> Не відповідає");
                return false;
            }
            editor.statusNotice("Модуль: " + module + " Назва: " + I7000.removeCRC(3, serialBuffer) + " -> Готовий");
        } catch (SerialPortException | InterruptedException e) {
            editor.statusError(e);
        }
        return true;
    }

    private void dataReadAction(String s) {
        serialBuffer.append(s);
    }

}
