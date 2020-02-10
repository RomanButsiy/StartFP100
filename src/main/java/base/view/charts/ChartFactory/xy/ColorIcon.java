package base.view.charts.ChartFactory.xy;

import java.awt.*;


/**
 * Copy of org.netbeans.lib.profiler.ui.components.ColorIcon.
 *
 * @author Jiri Sedlacek
 */
final class ColorIcon implements javax.swing.Icon {
    //~ Instance fields ----------------------------------------------------------------------------------------------------------

    private Color borderColor = Color.BLACK;
    private Color color = Color.BLACK;
    private int height = 5;
    private int width = 5;

    //~ Constructors -------------------------------------------------------------------------------------------------------------

    /** Creates a new instance of ColorIcon */
    public ColorIcon() {
    }

    public ColorIcon(Color color) {
        this();
        setColor(color);
    }

    public ColorIcon(Color color, int width, int height) {
        this(color);
        setIconWidth(width);
        setIconHeight(height);
    }

    public ColorIcon(Color color, Color borderColor, int width, int height) {
        this(color, width, height);
        setBorderColor(borderColor);
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setIconHeight(int height) {
        this.height = height;
    }

    public int getIconHeight() {
        return height;
    }

    public void setIconWidth(int width) {
        this.width = width;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
        if (color != null) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }

        if (borderColor != null) {
            g.setColor(borderColor);
            g.drawRect(x, y, width - 1, height - 1);
        }
    }
}
