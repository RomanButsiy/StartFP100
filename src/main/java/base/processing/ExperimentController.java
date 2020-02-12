package base.processing;

import base.Editor;
import base.PreferencesData;
import base.legacy.PApplet;
import base.view.ProgressBar.ProgressBar;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ExperimentController {

    private long timeStart = Long.parseLong(PreferencesData.get("chart.time.start", "75600000"));
    private final Editor editor;
    private final Experiment experiment;
    private ProgressBar progressBar;

    public ExperimentController(Editor editor, Experiment experiment) throws IOException {
        this.editor = editor;
        this.experiment = experiment;
        initExperiment();
    }

    public void exit() {
        if (experiment.isUntitledAndNotSaved()) {
            base.helpers.FileUtils.recursiveDelete(experiment.getFolder());
        }
    }

    private void initExperiment() throws IOException {
        if (experiment.isUntitledAndNotSaved()) return;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(experiment.getFile());
            load(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private void load(FileInputStream fileInputStream) {
        String[] lines = PApplet.loadStrings(fileInputStream);
        if (lines == null) return;
        List<String> loadedData = new ArrayList<>();
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '#') continue;
            int equals = line.indexOf('=');
            if (equals == -1) {
                loadedData.add(line);
            } else {
                parseKey(equals, line);
            }
        }
        editor.createTabs(PreferencesData.getInteger("runtime.count.modules", 0));
        addDataOnTabs(loadedData);
    }

    public synchronized void addDataOnTabs(List<String> buffer) {
        final int numberOfModules = PreferencesData.getInteger("runtime.count.modules", 0);
        final int responseTimeout = PreferencesData.getInteger("response.timeout", 200);
        if (numberOfModules == 0 || buffer.size() == 0) return;
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

}
