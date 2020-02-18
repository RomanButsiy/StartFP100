package base.helpers;

import SerialDriver.SerialDriver;
import base.Editor;
import base.PreferencesData;
import base.processing.Module;
import libraries.I7000;

import java.util.List;

import static base.helpers.BaseHelper.parsePortException;

public class CheckModules {
    private final Editor editor;
    private SerialDriver serialDriver;
    private StringBuffer serialBuffer;

    public CheckModules(Editor editor) {
        this.editor = editor;
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate", "115200");
        if (port == null) {
            editor.setEnabledItem(true);
            editor.statusError("Порт не вибрано");
            return;
        }
        editor.statusNotice("Перевірка модулів FP100...");
        editor.statusNotice("Порт: " + port + " | Швидкість: " + rate);
        serialBuffer = new StringBuffer();
        try {
            serialDriver = new SerialDriver(port, rate, this::dataReadAction);
        } catch (Exception e) {
            parsePortException(editor, e);
            editor.setEnabledItem(true);
            return;
        }
        List<Module> modules = editor.getExperiment().getModules();
        int responseTimeout = PreferencesData.getInteger("response.timeout", 200);
        for (Module module : modules) {
            module.setReady(SearchDevices(module.getModuleId(), responseTimeout));
        }
        serialDriver.dispose();
        editor.setEnabledItem(true);
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
        } catch (Exception e) {
            editor.statusError(e);
            return false;
        }
        return true;
    }

    private void dataReadAction(String s) {
        serialBuffer.append(s);
    }

}
