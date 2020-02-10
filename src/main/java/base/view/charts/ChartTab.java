package base.view.charts;

import base.Editor;
import base.view.charts.ChartFactory.ChartFactory;
import base.view.charts.ChartFactory.SimpleXYChartDescriptor;
import base.view.charts.ChartFactory.SimpleXYChartSupport;

import javax.swing.*;
import java.awt.*;

public class ChartTab extends JPanel {

    private SimpleXYChartSupport support;
    private Runtime runtime = Runtime.getRuntime();

    private String name;
    private Editor editor;

    public ChartTab(Editor editor, String name) {
        super(new BorderLayout());
        this.editor = editor;
        this.name = name;
        createModels();
        add(support.getChart());

    }

    private static final long SLEEP_TIME = 1000;
    private static final int VALUES_LIMIT = 150;
    private static final int ITEMS_COUNT = 1;

    private void createModels() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(-1000, 1000, 10000, 1f, true, VALUES_LIMIT);


        for (int i = 0; i < ITEMS_COUNT; i++) {
            descriptor.addLineFillItems("Item " + i);
        }

       // descriptor.setDetailsItems(new String[]{"Detail 1"});
        descriptor.setChartTitle("<html><font size='+1'><b>" + name + "</b></font></html>");
        descriptor.setXAxisDescription("<html>X Axis <i>[time]</i></html>");
        descriptor.setYAxisDescription("<html>Y Axis <i>[units]</i></html>");

        support = ChartFactory.createSimpleXYChart(descriptor);

        new Generator(support).start();
    }

    private static class Generator extends Thread {

        private SimpleXYChartSupport support;

        public void run() {
            while (true) {
                try {
                    long[] values = new long[ITEMS_COUNT];
                    for (int i = 0; i < values.length; i++) {
                        values[i] = (long) (-1000 * Math.random());
                    }
                    support.addValues(System.currentTimeMillis(), values);
                   // support.updateDetails(new String[]{});
                    Thread.sleep(SLEEP_TIME);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        private Generator(SimpleXYChartSupport support) {
            this.support = support;
        }
    }

    public String getPrettyName() {
        return name;
    }

}
