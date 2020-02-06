package base.processing;

import SerialDriver.SerialDriver;
import base.Editor;
import base.PreferencesData;

import static base.helpers.BaseHelper.parsePortException;

public class ExperimentProcessing implements Runnable{

    private final Editor editor;
    private final Experiment experiment;
    private SerialDriver serialDriver;
    private StringBuffer serialBuffer;
    private boolean stopExperiment;

    public ExperimentProcessing(Editor editor, Experiment experiment) {
        this.editor = editor;
        this.experiment = experiment;
    }

    public void stop() {
        stopExperiment = true;
    }

    public void start() {
        while (!stopExperiment){
            try {

                Thread.sleep(1000);
                System.out.println("dd");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate");
        serialBuffer = new StringBuffer();
        try {
            serialDriver = new SerialDriver(port, rate, this::dataReadAction);
        } catch (Exception e) {
            parsePortException(editor, e);
            experiment.stopExperiment();
            return;
        }
        stopExperiment = false;
        start();
        serialDriver.dispose();
    }

    private void dataReadAction(String s) {
        serialBuffer.append(s);
    }
}
