package base.view.charts.ChartFactory.xy;

import java.util.Collections;
import org.netbeans.lib.profiler.charts.ChartItem;
import org.netbeans.lib.profiler.charts.ItemPainter;
import org.netbeans.lib.profiler.charts.PaintersModel;

/**
 *
 * @author Jiri Sedlacek
 */
public class XYPaintersModel extends PaintersModel.Default {

    public XYPaintersModel(ChartItem[] items, ItemPainter[] painters) {
        super(items, painters);
    }

    public final void painterChanged(ItemPainter painter) {
        firePaintersChanged(Collections.singletonList(painter));
    }

}
