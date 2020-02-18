package base.processing;

import base.BaseInit;
import base.Editor;
import base.PreferencesData;
import base.helpers.FileUtils;
import base.legacy.PApplet;
import org.apache.commons.compress.utils.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExperimentController {

    private long timeStart = Long.parseLong(PreferencesData.get("chart.time.start", "75600000"));
    private Editor editor;
    private Experiment experiment;
    private boolean isHeader = true;

    public ExperimentController(Editor editor, Experiment experiment) throws Exception {
        this.editor = editor;
        this.experiment = experiment;
        initExperiment();
    }

    public void exit() {
        if (experiment.isUntitledAndNotSaved()) {
            base.helpers.FileUtils.recursiveDelete(experiment.getFolder());
        }
    }

    private void initExperiment() throws Exception {
        if (experiment.isUntitledAndNotSaved()) return;
        isHeader = false;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(experiment.getFile());
            load(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    public boolean isHeader() {
        return isHeader;
    }

    private void load(FileInputStream fileInputStream) throws Exception {
        String[] lines = PApplet.loadStrings(fileInputStream);
        if (lines == null) return;
        if (lines.length == 0 ) {
            fileEmpty();
            return;
        }
        List<String> loadedData = new ArrayList<>();
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '#') continue;
            int equals = line.indexOf('=');
            if (equals == -1) {
                if (!isHeader) {
                    invalidFileException();
                    return;
                }
                loadedData.add(line);
            } else {
                parseKey(equals, line);
            }
        }
        editor.createTabs(PreferencesData.getInteger("runtime.count.modules", 0) - 1);
        addDataOnTabs(loadedData);
    }

    private void fileEmpty() {
        JOptionPane.showMessageDialog(editor, "Файл пустий", "Помилка відкриття", JOptionPane.WARNING_MESSAGE);
    }

    private void invalidFileException() {
        JOptionPane.showMessageDialog(editor, "Некоректна структура файлу", "Помилка відкриття", JOptionPane.ERROR_MESSAGE);
    }

    public synchronized void addDataOnTabs(List<String> buffer) throws Exception {
        final int numberOfModules = PreferencesData.getInteger("runtime.count.modules", 0) - 1;
        final int responseTimeout = PreferencesData.getInteger("response.timeout", 200);
        if (numberOfModules <= 0 || buffer.size() == 0) return;
        int coefficient = editor.getTabs().get(0).getCoefficient();
        long[] timestamps = new long[buffer.size()];
        long[][][] values = new long[numberOfModules][buffer.size()][1];
        for (int t = 0; t < buffer.size(); t++) {
            timestamps[t] = timeStart;
            timeStart += responseTimeout;
            long[] val = getLong(buffer.get(t), coefficient);
            for (int i = 0; i < numberOfModules; i++) {
                values[i][t][0] = val[i+1];
            }
        }
        for (int i = 0; i < numberOfModules; i++) {
            editor.getTabs().get(i).setData(timestamps, values[i]);
        }
    }

    private void parseKey(int equals, String line) {
        String key = line.substring(0, equals).trim();
        String value = line.substring(equals + 1).trim();
        if (key.equals("title")) {
            setDefaultPreferences(value);
        }
    }

    private void setDefaultPreferences(String value) {
        List<String> values = (ArrayList<String>) toCollection(value);
        if (values.size() == 10) {
            int i = 0;
            PreferencesData.set("runtime.time", values.get(i++));
            PreferencesData.set("runtime.count.modules", values.get(i++));
            PreferencesData.set("response.timeout", values.get(i++));
            PreferencesData.set("analog.input.type", values.get(i++));
            PreferencesData.set("signal.out.range", values.get(i++));
            PreferencesData.set("signal.form", values.get(i++));
            PreferencesData.set("signal.form.period", values.get(i++));
            PreferencesData.set("signal.form.min", values.get(i++));
            PreferencesData.set("signal.form.max", values.get(i++));
            PreferencesData.set("signal.form.tau", values.get(i));
            if (PreferencesData.getInteger("runtime.count.modules", 0) > 0) isHeader = true;
        }
    }

    private Collection<String> toCollection(String value) {
        return Arrays.stream(value.split(","))
                .filter((v) -> !v.trim().isEmpty())
                .collect(Collectors.toList());
    }

    private long[] getLong(String str, int coefficient) {
        String[] s = str.split(",");
        long[] l = new long[s.length];
        for (int i = 0; i < s.length; i++) {
            try {
                l[i] = (long) (int) (Double.parseDouble(s[i]) * coefficient);
            } catch (NumberFormatException e) {
                l[i] = 0L;
                editor.statusError(e);
            }
        }
        return l;
    }

    public boolean saveAs() {
        FileDialog fd = new FileDialog(editor, "Зберегти як...", FileDialog.SAVE);
        fd.setDirectory(experiment.getFolder().getParentFile().getAbsolutePath());
        String oldName = experiment.getName();
        fd.setFile(oldName);
        fd.setVisible(true);
        String newParentDir = fd.getDirectory();
        String newName = fd.getFile();
        if (newName == null) return false;
        newName = checkName(newName);
        File newFolder;
        if (newName.endsWith(".fim") && newParentDir.endsWith(newName.substring(0, newName.lastIndexOf('.'))+ File.separator)) {
            newFolder = new File(newParentDir);
        } else {
            newFolder = new File(newParentDir, newName);
        }
        if (newFolder.equals(experiment.getFolder())) {
            return true;
        }
        try {
            String newPath = newFolder.getCanonicalPath() + File.separator;
            String oldPath = experiment.getFolder().getCanonicalPath() + File.separator;
            if (newPath.indexOf(oldPath) == 0) {
                BaseInit.showWarning("Як сюрреалістично!",
                        "Ви не можете зберегти експеримент\n" +
                                "в його власну теку. Це буде тривати вічно.", null);
                return false;
            }
        } catch (IOException ignored) { }
        if (newFolder.exists()) {
            FileUtils.recursiveDelete(newFolder);
        }
        try {
            experiment.saveAs(newFolder);
        } catch (IOException e) {
            BaseInit.showWarning("Помилка", e.getMessage(), null);
        }
        experiment.setUntitledAndNotSaved(false);
        return true;
    }

    private String checkName(String origName) {
        String newName = BaseInit.sanitizeName(origName);
        if (!newName.equals(origName)) {
            String msg = "Назва експерименту має бути змінена.\n" +
                            "Назви експериментів повинні починатися з літери чи цифри, а потім букви,\n" +
                            "цифри, тире, крапки та підкреслення. Максимальна довжина - 63 символи.";
            editor.statusError(msg);
        }
        return newName;
    }
}
