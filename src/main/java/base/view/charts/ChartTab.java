package base.view.charts;

import base.Editor;
import base.PreferencesData;
import base.view.charts.ChartFactory.ChartFactory;
import base.view.charts.ChartFactory.SimpleXYChartDescriptor;
import base.view.charts.ChartFactory.SimpleXYChartSupport;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.awt.*;

public class ChartTab extends JPanel {

    private SimpleXYChartSupport support;
    private Runtime runtime = Runtime.getRuntime();
    private String name;
    private Editor editor;
    private long minValue;
    private static long maxValue;
    private int coefficient;
    private float chartFactor;
    private String Y_Axis;
    private final int VALUES_LIMIT = PreferencesData.getInteger("chart.values.limit", 1000);
    private final int INPUT_TYPE = PreferencesData.getInteger("analog.input.type", 5);

    public ChartTab(Editor editor, String name) {
        super(new BorderLayout());
        this.editor = editor;
        this.name = name;
        initChartParam();
        createModels();
        add(support.getChart());
        support.setZoomingEnabled(true);
    }

    private void createModels() {
        SimpleXYChartDescriptor descriptor = SimpleXYChartDescriptor.decimal(minValue, maxValue, 0, chartFactor, false, VALUES_LIMIT);
        descriptor.addLineItems("Аналоговий сигнал");
        descriptor.setChartTitle("<html><font size='+1'><b>" + name + "</b></font></html>");
        descriptor.setYAxisDescription(Y_Axis);
        support = ChartFactory.createSimpleXYChart(descriptor);
    }

    public void setData(long[] time, long[][] values) {
        for(int i = 0; i < time.length; i++) {
            support.addValues(time[i], values[i]);
        }
    }

    public String getPrettyName() {
        return name;
    }

    public int getCoefficient() {
        return coefficient;
    }

    private void initChartParam() {
        if (PreferencesData.getInteger("signal.type", 0) == 1) {
            coefficient = 100;
            minValue = -10000;
            maxValue = 10000;
            Y_Axis = "%";
            chartFactor = 1f / coefficient;
            return;
        }
        if (INPUT_TYPE == 0) {
            coefficient = 1000;
            minValue = -15000;
            maxValue = 15000;
            Y_Axis = "мВ";
        }
        if (INPUT_TYPE == 1) {
            coefficient = 1000;
            minValue = -50000;
            maxValue = 50000;
            Y_Axis = "мВ";
        }
        if (INPUT_TYPE == 2) {
            coefficient = 100;
            minValue = -10000;
            maxValue = 10000;
            Y_Axis = "мВ";
        }
        if (INPUT_TYPE == 3) {
            coefficient = 100;
            minValue = -50000;
            maxValue = 50000;
            Y_Axis = "мВ";
        }
        if (INPUT_TYPE == 4) {
            coefficient = 10000;
            minValue = -10000;
            maxValue = 10000;
            Y_Axis = "Вольт";
        }
        if (INPUT_TYPE == 5) {
            coefficient = 10000;
            minValue = -25000;
            maxValue = 25000;
            Y_Axis = "Вольт";
        }
        if (INPUT_TYPE == 6) {
            coefficient = 1000;
            minValue = -20000;
            maxValue = 20000;
            Y_Axis = "мВ";
        }
        chartFactor = 1f / coefficient;
    }

}
