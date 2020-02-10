package base.view.charts.ChartFactory.xy;

import java.text.NumberFormat;
import org.netbeans.lib.profiler.charts.axis.AxisMark;
import org.netbeans.lib.profiler.charts.axis.LongMark;
import org.netbeans.lib.profiler.charts.axis.SimpleLongMarksPainter;

/**
 *
 * @author Jiri Sedlacek
 */
public class XYDecimalMarksPainter extends SimpleLongMarksPainter {

    protected final double factor;
    protected final NumberFormat format;


    public XYDecimalMarksPainter(double factor, NumberFormat format) {
        this.factor = factor;
        this.format = format;
    }


    protected String formatMark(AxisMark mark) {
        return format.format(((LongMark)mark).getValue() * factor);
    }

}
