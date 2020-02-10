package base.view.charts.ChartFactory.xy;

import java.awt.Color;

/**
 *
 * @author Jiri Sedlacek
 */
public interface XYTooltipModel {

    public String getTimeValue      (long timestamp);

    public int    getRowsCount      ();
    public String getRowName        (int index);
    public Color  getRowColor       (int index);
    public String getRowValue       (int index, long itemValue);

}
