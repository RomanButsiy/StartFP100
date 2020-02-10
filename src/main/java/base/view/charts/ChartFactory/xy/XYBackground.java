package base.view.charts.ChartFactory.xy;

import org.netbeans.lib.profiler.charts.ChartContext;
import org.netbeans.lib.profiler.charts.ChartDecorator;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.lib.profiler.charts.swing.Utils;

/**
 *
 * @author Jiri Sedlacek
 */
public class XYBackground implements ChartDecorator {

    private static final Color GRADIENT_TOP = new Color(240, 240, 240);
    private static final Color GRADIENT_BOTTOM = new Color(250, 250, 250);

    public void paint(Graphics2D g, Rectangle dirtyArea, ChartContext context) {
        if (Utils.forceSpeed()) g.setPaint(GRADIENT_BOTTOM);
        else g.setPaint(new GradientPaint(
                new Point(0, Utils.checkedInt(context.getViewportOffsetY())),
                GRADIENT_TOP,
                new Point(0, context.getViewportHeight()),
                GRADIENT_BOTTOM));
        g.fill(dirtyArea);
    }

}
