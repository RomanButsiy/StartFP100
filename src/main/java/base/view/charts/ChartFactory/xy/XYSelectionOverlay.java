package base.view.charts.ChartFactory.xy;

import org.netbeans.lib.profiler.charts.ChartComponent;
import org.netbeans.lib.profiler.charts.ChartOverlay;
import org.netbeans.lib.profiler.charts.ChartSelectionListener;
import org.netbeans.lib.profiler.charts.ItemSelection;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.netbeans.lib.profiler.charts.ChartConfigurationListener;
import org.netbeans.lib.profiler.charts.ChartContext;
import org.netbeans.lib.profiler.charts.swing.Utils;
import org.netbeans.lib.profiler.charts.xy.XYItemSelection;

/**
 *
 * @author Jiri Sedlacek
 */
public class XYSelectionOverlay extends ChartOverlay {

    private ChartComponent chart;

    private int selectionExtent;

    private final ConfigurationListener configurationListener;
    private final SelectionListener selectionListener;
    private final Set<Point> selectedValues;

    private Paint markPaint;
    private Paint oddPerfPaint;
    private Paint evenPerfPaint;

    private Stroke markStroke;
    private Stroke oddPerfStroke;
    private Stroke evenPerfStroke;


    public XYSelectionOverlay() {
        configurationListener = new ConfigurationListener();
        selectionListener = new SelectionListener();
        selectedValues = new HashSet();
        initDefaultValues();
    }


    // --- Public API ----------------------------------------------------------

    public final void registerChart(ChartComponent chart) {
        unregisterListener();
        this.chart = chart;
        registerListener();
    }

    public final void unregisterChart(ChartComponent chart) {
        unregisterListener();
        this.chart = null;
    }


    // --- Private implementation ----------------------------------------------

    private void registerListener() {
        if (chart == null) return;
        chart.addConfigurationListener(configurationListener);
        chart.getSelectionModel().addSelectionListener(selectionListener);
    }

    private void unregisterListener() {
        if (chart == null) return;
        chart.removeConfigurationListener(configurationListener);
        chart.getSelectionModel().removeSelectionListener(selectionListener);
    }

    private void initDefaultValues() {
        markPaint = new Color(80, 80, 80);
        oddPerfPaint = Color.BLACK;
        evenPerfPaint = Color.WHITE;

        markStroke = new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        oddPerfStroke = new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[] { 1.0f, 3.0f }, 0);
        evenPerfStroke = new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[] { 1.0f, 3.0f }, 2);

        selectionExtent = 3;
    }


    public void paint(Graphics g) {
        if (selectedValues.isEmpty()) return;

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHints(chart.getRenderingHints());

        Iterator<Point> it = selectedValues.iterator();
        boolean linePainted = false;

        while (it.hasNext()) {
            Point p = it.next();

            if (!linePainted) {
                g2.setPaint(evenPerfPaint);
                g2.setStroke(evenPerfStroke);
                g2.drawLine(p.x, 0, p.x, getHeight());
                g2.setPaint(oddPerfPaint);
                g2.setStroke(oddPerfStroke);
                g2.drawLine(p.x, 0, p.x, getHeight());

                g2.setPaint(markPaint);
                g2.setStroke(markStroke);

                linePainted = true;
            }

            g2.fillOval(p.x - selectionExtent + 1, p.y - selectionExtent + 1,
                    selectionExtent * 2 - 1, selectionExtent * 2 - 1);
        }

    }

    private void vLineBoundsChanged(Set<Point> oldSelection, Set<Point> newSelection) {
        Point oldSel = oldSelection.isEmpty() ? null : oldSelection.iterator().next();
        Point newSel = newSelection.isEmpty() ? null : newSelection.iterator().next();

        if (oldSel != null) repaint(oldSel.x - selectionExtent, 0,
                selectionExtent * 2, getHeight());
        if (newSel != null) repaint(newSel.x - selectionExtent, 0,
                selectionExtent * 2, getHeight());
    }

    private static void updateSelectedValues(Set<Point> selectedValues,
                                             List<ItemSelection> selectedItems,
                                             ChartContext context) {
        selectedValues.clear();
        for (ItemSelection sel : selectedItems) {
            XYItemSelection xySel = (XYItemSelection)sel;
            long xValue = xySel.getItem().getXValue(xySel.getValueIndex());
            long yValue = xySel.getItem().getYValue(xySel.getValueIndex());
            selectedValues.add(new Point(Utils.checkedInt(Math.ceil(context.getViewX(xValue))),
                    Utils.checkedInt(Math.ceil(context.getViewY(yValue)))));
        }
    }


    private class ConfigurationListener extends ChartConfigurationListener.Adapter {
        public void contentsUpdated(long offsetX, long offsetY,
                                    double scaleX, double scaleY,
                                    long lastOffsetX, long lastOffsetY,
                                    double lastScaleX, double lastScaleY,
                                    int shiftX, int shiftY) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Set<Point> oldSelectedValues = new HashSet(selectedValues);
                    updateSelectedValues(selectedValues, chart.getSelectionModel().getHighlightedItems(), chart.getChartContext());
                    vLineBoundsChanged(oldSelectedValues, selectedValues);
                }
            });
        }
    }

    private class SelectionListener implements ChartSelectionListener {

        public void selectionModeChanged(int newMode, int oldMode) {}

        public void selectionBoundsChanged(Rectangle newBounds, Rectangle oldBounds) {}

        public void selectedItemsChanged(List<ItemSelection> currentItems,
                                         List<ItemSelection> addedItems, List<ItemSelection> removedItems) {}

        public void highlightedItemsChanged(List<ItemSelection> currentItems,
                                            List<ItemSelection> addedItems, List<ItemSelection> removedItems) {
            Set<Point> oldSelectedValues = new HashSet(selectedValues);
            updateSelectedValues(selectedValues, currentItems, chart.getChartContext());
            vLineBoundsChanged(oldSelectedValues, selectedValues);
        }

    }
}
