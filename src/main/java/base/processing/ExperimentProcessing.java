package base.processing;

import SerialDriver.SerialDriver;
import base.Editor;
import base.PreferencesData;
import base.legacy.PApplet;
import libraries.I7000;
import org.apache.commons.compress.utils.IOUtils;

import java.io.PrintWriter;;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static base.helpers.BaseHelper.parsePortException;

public class ExperimentProcessing implements Runnable {

    private final Editor editor;
    private final Experiment experiment;
    private SerialDriver serialDriver;
    private StringBuffer serialBuffer;
    private volatile boolean stopExperiment;
    private AtomicBoolean useFirstBuffer = new AtomicBoolean(true);
    private List<String> bufferOne = Collections.synchronizedList(new ArrayList<>());
    private List<String> bufferTwo = Collections.synchronizedList(new ArrayList<>());
    private Timer checkErrorStatusTimer;
    private Timer getNewDataTimer;
    private volatile Integer[] err;

    public ExperimentProcessing(Editor editor, Experiment experiment) {
        this.editor = editor;
        this.experiment = experiment;
    }

    public void stop() {
        stopExperiment = true;
        checkErrorStatusTimer.cancel();
    }

    public void stopAll() {
        editor.handleStopAll();
    }

    public void start() throws Exception {
        StringBuilder result = new StringBuilder();
        float[] signal = experiment.generateSignal();
        String[] commands = generateDacCommands(signal);
        String[] otherCommands = generateOtherCommands();
        //String synchronizedSampling = I7000.getSynchronizedSampling();
        int responseTimeout = PreferencesData.getInteger("response.timeout");
        int period = responseTimeout / (otherCommands.length + 1) - 5;
        long timeAll, time;
        boolean flag;
        int sErr;
        while (!stopExperiment) {
            for (int i = 0; i < commands.length; i++) {
                timeAll = System.currentTimeMillis();
                serialBuffer.setLength(0);
                result.setLength(0);
                time = System.currentTimeMillis();
                sErr = 0;
                flag = false;
                if (!commands[i].equals("null")) {
                    serialDriver.write(commands[i]);
                    while ((System.currentTimeMillis() - time < period)) {
                        if (serialBuffer.indexOf("\r") != -1) {
                            flag = true;
                            if (serialBuffer.indexOf(">") == -1) {
                                err[sErr]++;
                                break;
                            }
                            err[sErr] = 0;
                            break;
                        }
                        Thread.sleep(1);
                    }
                    result.append(signal[i]);
                    if (!flag) err[sErr]++;
                } else {
                    result.append(0);
                }
                sErr++;
                if (stopExperiment) return;
                //serialDriver.write(synchronizedSampling);
                for (String command : otherCommands) {
                    result.append(",");
                    serialBuffer.setLength(0);
                    serialDriver.write(command);
                    time = System.currentTimeMillis();
                    flag = false;
                    while ((System.currentTimeMillis() - time < period)) {
                        if (serialBuffer.indexOf("\r") != -1) {
                            flag = true;
                            if (serialBuffer.indexOf(">") == -1) {
                                err[sErr]++;
                                result.append("0");
                                break;
                            }
                            result.append(I7000.removeCRC(1, serialBuffer));
                            err[sErr] = 0;
                            break;
                        }
                        Thread.sleep(1);
                    }
                    if (!flag) {
                        err[sErr]++;
                        result.append("0");
                    }
                    sErr++;
                }
                if (useFirstBuffer.get()) {
                    bufferOne.add(String.valueOf(result));
                } else {
                    bufferTwo.add(String.valueOf(result));
                }
                while ((System.currentTimeMillis() - timeAll < responseTimeout)) {
                    Thread.sleep(1);
                }
                if (stopExperiment) return;
            }
        }

    }

    private String[] generateOtherCommands() {
        String dacId = PreferencesData.get("runtime.dac.module", "");
        List<Module> modules = experiment.getModules();
        String[] str = new String[PreferencesData.getInteger("runtime.count.modules") - 1];
        int i = 0;
        for (Module module : modules) {
            if (module.getModuleId().equals(dacId)) continue;
            if (module.isActive() && module.isReady()) {
                //str[i++] = I7000.setAnalogInTechnicalUnitsSynchronized(module.getModuleId());
                str[i++] = I7000.setAnalogInTechnicalUnits(module.getModuleId());
            }
        }
        return str;
    }

    private String[] generateDacCommands(float[] signal) {
        if (!PreferencesData.getBoolean("runtime.dac.module.ready", false)) return new String[]{"null"};
        String dacId = PreferencesData.get("runtime.dac.module");
        String[] str = new String[signal.length];
        for (int i = 0; i < signal.length; i++) {
            str[i] = I7000.setAnalogOutTechnicalUnits(dacId, signal[i]);
        }
        return str;
    }

    @Override
    public void run() {
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate");
        serialBuffer = new StringBuffer();
        checkErrorStatus();
        try {
            serialDriver = new SerialDriver(port, rate, s -> serialBuffer.append(s));
        } catch (Exception e) {
            parsePortException(editor, e);
            editor.stopExperimentPortException();
            return;
        }
        stopExperiment = false;
        bufferOne.clear();
        bufferTwo.clear();
        getNewData();
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        serialDriver.dispose();
    }

    private void getNewData() {
        getNewDataTimer = new Timer(ExperimentProcessing.class.getName());
        int period = PreferencesData.getInteger("response.timeout", 200);
        int chartTimeUpdate = PreferencesData.getInteger("chart.time.update", 1000);
        if (chartTimeUpdate < 1 ) chartTimeUpdate = 1;
        getNewDataTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                toggle();
                PrintWriter writer = null;
                try {
                    writer = PApplet.createWriter(experiment.getFile(), true);
                    Thread.sleep(2 * period / 3);
                    if (useFirstBuffer.get()) {
                        addDataOnTabs(bufferTwo);
                        for (String str : bufferTwo) writer.println(str);
                        bufferTwo.clear();
                    } else {
                        addDataOnTabs(bufferOne);
                        for (String str : bufferOne) writer.println(str);
                        bufferOne.clear();
                    }
                } catch (Exception e) {
                    editor.statusError(e);
                    stopAll();
                } finally {
                    IOUtils.closeQuietly(writer);
                }
                if (stopExperiment) {
                    editor.getProgressBar().closeProgressBar();
                    editor.getToolbar().deactivateStop();
                    editor.setLineStatusText("Експеримент зупинено");
                    getNewDataTimer.cancel();
                }
            }
        }, chartTimeUpdate, chartTimeUpdate);
    }

    private void checkErrorStatus() {
        err = new Integer[PreferencesData.getInteger("runtime.count.modules")];
        Arrays.fill(err, 0);
        int period = PreferencesData.getInteger("response.timeout", 200) * 3;
        checkErrorStatusTimer = new Timer(ExperimentProcessing.class.getName());
        checkErrorStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (IntStream.range(0, err.length).filter(i -> err[i] > 2).findFirst().orElse(-1) != -1) {
                    editor.statusError("Помилка очікування");
                    editor.statusError("Модуль не відповідає");
                    stopAll();
                }
            }
        }, 0, period);
    }

    private void addDataOnTabs(List<String> buffer) throws Exception {
        editor.getExperimentController().addDataOnTabs(buffer);
        // need fix
    }

    protected void toggle() {
        boolean temp;
        do {
            temp = useFirstBuffer.get();
        } while(!useFirstBuffer.compareAndSet(temp, !temp));
    }

}
