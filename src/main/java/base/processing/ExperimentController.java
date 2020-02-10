package base.processing;

import base.Editor;
import base.PreferencesData;
import base.legacy.PApplet;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExperimentController {

    private final Editor editor;
    private final Experiment experiment;

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
        List<String> loadData = new ArrayList<>();
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '#') continue;
            int equals = line.indexOf('=');
            if (equals == -1) {

            } else {
                parseKey(equals, line);
            }

        }
    }

    public void addDataOnTabs(List<String> buffer) {
        int numberOfModules = PreferencesData.getInteger("runtime.count.modules", 0);
        if (numberOfModules == 0 || buffer.isEmpty()) return;

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
        if (values.size() == 8) {
            PreferencesData.set("runtime.time", values.get(0));
            PreferencesData.set("runtime.count.modules", values.get(1));
            PreferencesData.set("response.timeout", values.get(2));
            PreferencesData.set("signal.out.range", values.get(3));
            PreferencesData.set("signal.form", values.get(4));
            PreferencesData.set("signal.form.period", values.get(5));
            PreferencesData.set("signal.form.min", values.get(6));
            PreferencesData.set("signal.form.max", values.get(7));
        }
    }

    private Collection<String> toCollection(String value) {
        return Arrays.stream(value.split(","))
                .filter((v) -> !v.trim().isEmpty())
                .collect(Collectors.toList());
    }

}
