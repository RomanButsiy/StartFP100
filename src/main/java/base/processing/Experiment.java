package base.processing;


import base.Editor;
import base.PreferencesData;
import base.legacy.PApplet;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Experiment {

    private final Editor editor;
    private String name;
    private File folder;
    private File file;
    private boolean isUntitledAndNotSaved = false;
    private boolean isExperimentRunning = false;
    private boolean isRuntimeRunning = false;
    private final ExperimentProcessing experimentProcessing;

    public Experiment(Editor editor, File file, String name) throws IOException {
        this.folder = file.getParentFile();
        this.file = file;
        this.name = name;
        this.editor = editor;
        experimentProcessing = new ExperimentProcessing(editor, this);
    }

    public File getFile() {
        return file;
    }

    public File getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }

    static public File checkExperimentFile(File file) {
        String fileName = file.getName();
        File parent = file.getParentFile();
        String parentName = parent.getName();
        String fimName = parentName + ".fim";
        File altFimFile = new File(parent, fimName);

        if (fimName.equals(fileName))
            return file;

        if (altFimFile.exists())
            return altFimFile;

        return null;
    }

    public boolean isUntitledAndNotSaved() {
        return isUntitledAndNotSaved;
    }

    public void setUntitledAndNotSaved(boolean untitledAndNotSaved) {
        isUntitledAndNotSaved = untitledAndNotSaved;
    }

    public boolean isExperimentRunning() {
        return isExperimentRunning;
    }

    public boolean isRuntimeRunning() {
        return isRuntimeRunning;
    }

    public void setExperimentRunning(boolean experimentRunning) {
        isExperimentRunning = experimentRunning;
        PreferencesData.setBoolean("runtime.experiment.running", experimentRunning);
    }

    public void runExperiment() throws Exception{
        setExperimentRunning(true);
        if (isUntitledAndNotSaved) setFileHeader();
        setUntitledAndNotSaved(false);
        PreferencesData.set("runtime.last.experiment.running", name);
        PreferencesData.set("last.experiment.path", file.getAbsolutePath());
        PreferencesData.save();
        isRuntimeRunning = true;
        new Thread(experimentProcessing).start();
        Thread closeHook = new Thread(experimentProcessing::stopAll);
        closeHook.setName("ExperimentProcessing closeHook");
        Runtime.getRuntime().addShutdownHook(closeHook);

    }

    private void setFileHeader() {
        List<String> title = new ArrayList<>();
        String description = "# Time, Number of ADC(s), Timeout, Analog input type, Type of range, Signal form, Period, Min, Max";
        title.add(String.valueOf(java.time.Clock.systemUTC().instant()));
        title.add(PreferencesData.get("runtime.count.modules", "0"));
        title.add(PreferencesData.get("response.timeout"));
        title.add(PreferencesData.get("analog.input.type"));
        title.add(PreferencesData.get("signal.out.range"));
        title.add(PreferencesData.get("signal.form"));
        title.add(PreferencesData.get("signal.form.period"));
        title.add(PreferencesData.get("signal.form.min"));
        title.add(PreferencesData.get("signal.form.max"));
        PrintWriter writer = null;
        try {
            writer = PApplet.createWriter(getFile(), true);
            writer.println(description);
            writer.print("title=");
            writer.println(String.join(",", title));
        } catch (Exception e) {
            editor.statusError("Не вдалося записати дані експерименту у файл: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public float round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }

    public void stopExperiment() {
        experimentProcessing.stop();
        setExperimentRunning(false);
        PreferencesData.set("last.experiment.path", "");
        PreferencesData.save();
    }

    public float[] generateSignal() {
        double signalMax = PreferencesData.getDouble("signal.form.max");
        double signalMin = PreferencesData.getDouble("signal.form.min");
        int signalPeriod = PreferencesData.getInteger("signal.form.period");
        int signalForm = PreferencesData.getInteger("signal.form");
        int responseTimeout = PreferencesData.getInteger("response.timeout");
        double stepDouble = 1f * signalPeriod / responseTimeout;
        int step = (int) Math.round(stepDouble);
        float[] signal = new float[step];
        if (signalForm == 0) { //Синусоїда
            double sinStep = 2 * Math.PI / stepDouble;
            double difference = (signalMax - signalMin) / 2.0;
            for (int i = 0; i < step; i++) {
                signal[i] = round((difference * Math.sin(i * sinStep) + difference + signalMin), 3);
            }
            return signal;
        }
        if (signalForm == 1) { //Трапеція

        }
        if (signalForm == 2) { //Трикутник

        }
        // Інший
        return new float[]{8.640f, 2.230f, 1.100f, 8.000f, 1.520f, 5.250f, 5.341f, 5.341f, 5.341f, 5.341f,
                5.341f, 5.341f, 8.640f, 2.230f, 1.100f, 8.000f, 1.520f, 5.250f, 5.341f, 5.341f,
                5.341f, 5.341f, 8.640f, 2.230f, 1.100f, 8.000f, 1.520f, 5.250f, 5.341f, 5.341f,
                5.341f, 5.341f, 8.640f, 2.230f, 1.100f, 8.000f, 1.520f, 5.250f, 5.341f, 5.341f,
                5.341f, 5.341f, 8.640f, 2.230f, 1.100f, 8.000f, 1.520f, 5.250f, 5.341f, 5.341f,};
    }
}
