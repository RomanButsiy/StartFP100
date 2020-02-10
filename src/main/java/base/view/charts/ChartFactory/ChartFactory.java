package base.view.charts.ChartFactory;

/**
 * Factory class to create custom charts.
 *
 * @author Jiri Sedlacek
 */
public final class ChartFactory {

    /**
     * Creates an instance of SimpleXYChartSupport representing a simple XY chart.
     *
     * @param descriptor chart descriptor
     * @return instance of SimpleXYChartSupport representing a simple XY chart
     */
    public static SimpleXYChartSupport createSimpleXYChart(SimpleXYChartDescriptor descriptor) {
        return new SimpleXYChartSupport(descriptor.getChartTitle(),
                descriptor.getXAxisDescription(),
                descriptor.getYAxisDescription(),
                descriptor.getChartType(),
                descriptor.getInitialYMargin(),
                descriptor.getItemNames(),
                descriptor.getItemColors(),
                descriptor.getLineWidths(),
                descriptor.getLineColors(),
                descriptor.getFillColors1(),
                descriptor.getFillColors2(),
                descriptor.getMinValue(),
                descriptor.getMaxValue(),
                descriptor.getChartFactor(),
                descriptor.getCustomFormat(),
                descriptor.areItemsHideable(),
                descriptor.getValuesBuffer(),
                descriptor.getDetailsItems());
    }


    private ChartFactory() {}

}