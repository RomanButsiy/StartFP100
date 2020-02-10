package base.view.charts.ChartFactory.xy;

import org.netbeans.lib.profiler.charts.axis.AxisMark;
import org.netbeans.lib.profiler.charts.axis.LongMark;
import org.netbeans.lib.profiler.charts.axis.PercentLongMarksPainter;

/**
 *
 * @author Jiri Sedlacek
 */
public class XYPercentMarksPainter extends PercentLongMarksPainter {

    protected final double factor;


    public XYPercentMarksPainter(long minValue, long maxValue, double factor) {
        super(minValue, maxValue);
        this.factor = factor;
    }


    protected String formatMark(AxisMark mark) {
        if (!(mark instanceof LongMark)) return mark.toString();
        double value = ((LongMark)mark).getValue();
        double relValue = (value - minValue) / maxValue * factor;
        return format.format(relValue);
    }

}
