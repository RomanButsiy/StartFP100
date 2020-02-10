package base.view.charts.ChartFactory;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import base.view.charts.ChartFactory.xy.SimpleXYChartUtils;
import base.view.charts.ChartFactory.xy.XYPaintersModel;
import base.view.charts.ChartFactory.xy.XYStorage;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItemsModel;

/**
 * Handle to access a simple XY chart. The chart supports adding new data and
 * updating the details area.
 *
 * @author Jiri Sedlacek
 */
public final class SimpleXYChartSupport {

    private final static Logger LOGGER = Logger.getLogger(SimpleXYChartSupport.class.getName());

    // --- Public chart boundary constants -------------------------------------

    public static final long MIN_UNDEFINED = Long.MAX_VALUE;
    public static final long MAX_UNDEFINED = Long.MIN_VALUE;


    // --- Instance variables --------------------------------------------------

    private final String chartTitle;
    private final String xAxisDescription;
    private final String yAxisDescription;

    private final int chartType;
    private final boolean hideItems;
    private final Color[] itemColors;
    private final long initialYMargin;
    private final String[] detailsItems;
    private final double chartFactor;
    private final NumberFormat customFormat;

    private final XYStorage storage;
    private final SynchronousXYItemsModel itemsModel;
    private final XYPaintersModel paintersModel;

    private JComponent chartUI;
    private SimpleXYChartUtils.DetailsHandle detailsHandle;
    private boolean legendVisible;
    private boolean zoomingEnabled;


    // --- Public interface ----------------------------------------------------

    /**
     * Returns a JComponent containing the chart.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @return JComponent containing the chart;
     */
    public JComponent getChart() {
        if (chartUI == null) {
            chartUI = SimpleXYChartUtils.createChartUI(chartTitle, xAxisDescription,
                    yAxisDescription, chartType,
                    itemColors, initialYMargin,
                    hideItems, legendVisible,
                    zoomingEnabled, chartFactor,
                    customFormat, storage,
                    itemsModel, paintersModel);
            if (detailsItems != null)
                detailsHandle = SimpleXYChartUtils.createDetailsArea(detailsItems,
                        chartUI);
        }
        return chartUI;
    }

    /**
     * Saves chart values into the provided OutputStream. This method should not
     * be called in the Event Dispatch Thread.
     *
     * @param os OuptutStream into which to save the chart values
     * @throws IOException if an I/O error occurs
     */
    public void saveValues(OutputStream os) throws IOException {
        storage.saveValues(os);
    }

    /**
     * Loads chart values from the provided InputStream. This method should not
     * be called in the Event Dispatch Thread.
     *
     * @param is InputStram from which to load the chart values
     * @throws IOException if an I/O error occurs
     */
    public void loadValues(InputStream is) throws IOException {
        storage.loadValues(is);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                itemsModel.valuesAdded();
                // Do not catch ProfilerXYItemsModel: new timestamp T1 not greater than previous T0, skipping the values.
                // Should be synchronized since the originally saved model had to be synchronized
            }
        });
    }

    /**
     * Adds a packet of values.
     * <br><br><b>Note:</b> This method can be called from any thread.
     *
     * @param timestamp timestamp of the data packet
     * @param values data packet
     */
    public void addValues(final long timestamp, final long[] values) {
        Runnable valuesUpdater = new Runnable() {
            public void run() {
                storage.addValues(timestamp, values);
                try {
                    itemsModel.valuesAdded();
                } catch (IllegalArgumentException e) {
                    // ProfilerXYItemsModel: new timestamp T1 not greater than previous T0, skipping the values.
                    LOGGER.log(Level.INFO, "Results not synchronized", e); // NOI18N
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) valuesUpdater.run();
        else SwingUtilities.invokeLater(valuesUpdater);
    }

    /**
     * Updates the details area of the chart.
     * <br><br><b>Note:</b> This method can be called from any thread.
     *
     * @param details details packet
     */
    public void updateDetails(final String[] details) {
        Runnable detailsUpdater = new Runnable() {
            public void run() {
                detailsHandle.updateDetails(details); }
        };
        if (SwingUtilities.isEventDispatchThread()) detailsUpdater.run();
        else SwingUtilities.invokeLater(detailsUpdater);
    }


    /**
     * Shows or hides legend section of the chart.
     * <br><br><b>Note:</b> This method can be called from any thread.
     *
     * @param visible new visibility of the legend section of the chart
     *
     * @since VisualVM 1.3.4
     */
    public void setLegendVisible(final boolean visible) {
        Runnable visibilityUpdater = new Runnable() {
            public void run() {
                if (legendVisible == visible) return;
                legendVisible = visible;
                if (chartUI != null)
                    SimpleXYChartUtils.setLegendVisible(chartUI, legendVisible);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) visibilityUpdater.run();
        else SwingUtilities.invokeLater(visibilityUpdater);
    }

    /**
     * Returns true if legend section of the chart is visible, false otherwise.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @return true if legend section of the chart is visible, false otherwise
     *
     * @since VisualVM 1.3.4
     */
    public boolean isLegendVisible() {
        return legendVisible;
    }


    /**
     * Enables or disables zooming the chart data.
     * <br><br><b>Note:</b> This method can be called from any thread.
     * <br><br><b>Warning:</b> Displaying live data by a zoomed chart may result
     * in incorrect appearance once the data buffer starts dropping oldest values.
     *
     * @param zooming true if zooming is enabled, false otherwise
     *
     * @since VisualVM 1.3.8
     */
    public void setZoomingEnabled(final boolean zooming) {
        Runnable visibilityUpdater = new Runnable() {
            public void run() {
                if (zoomingEnabled == zooming) return;
                zoomingEnabled = zooming;
                if (chartUI != null)
                    SimpleXYChartUtils.setZoomingEnabled(chartUI, zoomingEnabled);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) visibilityUpdater.run();
        else SwingUtilities.invokeLater(visibilityUpdater);
    }

    /**
     * Returns true if charts zooming is enabled, false otherwise.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @return true if charts zooming is enabled, false otherwise
     *
     * @since VisualVM 1.3.8
     */
    public boolean isZoomingEnabled() {
        return zoomingEnabled;
    }


    /**
     * Formats a decimal value to String. Use this method to make sure the value
     * appears in the same format as used in the chart (tooltip). Useful for
     * updating details area.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @param value value to be formatted
     * @return formatted value in the same format as used in the chart
     */
    public String formatDecimal(long value) {
        return SimpleXYChartUtils.formatDecimal(value * chartFactor, customFormat);
    }

    /**
     * Formats a bytes value to String. Use this method to make sure the value
     * appears in the same format as used in the chart (tooltip). Useful for
     * updating details area.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @param value value to be formatted
     * @return formatted value in the same format as used in the chart
     */
    public String formatBytes(long value) {
        return SimpleXYChartUtils.formatBytes((long)(value * chartFactor));
    }

    /**
     * Formats a percent value to String. Use this method to make sure the value
     * appears in the same format as used in the chart (tooltip). Useful for
     * updating details area.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @param value value to be formatted
     * @return formatted value in the same format as used in the chart
     */
    public String formatPercent(long value) {
        return SimpleXYChartUtils.formatPercent(value * chartFactor);
    }

    /**
     * Formats a time value to String. Use this method to make sure the value
     * appears in the same format as used in the chart (tooltip). Useful for
     * updating details area.
     * <br><br><b>Note:</b> This method must be called in the Event Dispatch Thread.
     *
     * @param value value to be formatted
     * @return formatted value in the same format as used in the chart
     */
    public String formatTime(long value) {
        int timestamps = storage.getTimestampsCount();
        if (timestamps == 0) return SimpleXYChartUtils.formatTime(value, value, value);
        else return SimpleXYChartUtils.formatTime(value, storage.getTimestamp(0),
                storage.getTimestamp(timestamps - 1));
    }


    // --- Internal constructors -----------------------------------------------

    SimpleXYChartSupport(String chartTitle, String xAxisDescription, String yAxisDescription,
                         int chartType, long initialYMargin, String[] itemNames, Color[] itemColors,
                         float[] lineWidths, Color[] lineColors, Color[] fillColors1, Color[] fillColors2,
                         long minValue, long maxValue, double chartFactor, NumberFormat customFormat,
                         boolean hideItems, int valuesBuffer, String[] detailsItems) {

        this.chartTitle = chartTitle;
        this.xAxisDescription = xAxisDescription;
        this.yAxisDescription = yAxisDescription;

        this.chartType = chartType;
        this.hideItems = hideItems;
        this.itemColors = itemColors;
        this.initialYMargin = initialYMargin;
        this.detailsItems = detailsItems;
        this.chartFactor = chartFactor;
        this.customFormat = customFormat;

        storage = SimpleXYChartUtils.createStorage(valuesBuffer);
        itemsModel = SimpleXYChartUtils.createItemsModel(storage, itemNames, minValue, maxValue);
        paintersModel = SimpleXYChartUtils.createPaintersModel(lineWidths, lineColors,
                fillColors1, fillColors2, itemsModel);

        legendVisible = true;
        zoomingEnabled = false;
    }

}
