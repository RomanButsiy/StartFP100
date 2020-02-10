package base.view.charts.ChartFactory.uisupport;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.netbeans.lib.profiler.ui.UIUtils;

/**
 * Simple thin JSeparator.
 *
 * @author Jiri Sedlacek
 */
public final class SeparatorLine extends JSeparator {

    private final Color separatorColor;


    public SeparatorLine() {
        this(HORIZONTAL);
    }

    public SeparatorLine(boolean thin) {
        this(HORIZONTAL, thin);
    }

    public SeparatorLine(int orientation) {
        this(orientation, UISupport.isAquaLookAndFeel());
    }

    public SeparatorLine(int orientation, boolean thin) {
        super(orientation);
        setBorder(BorderFactory.createEmptyBorder());
        separatorColor = thin ? getSeparatorColor() : null;
    }


    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        if (separatorColor != null) dim.height = 1;
        return dim;
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }


    public void paint(Graphics g) {
        if (separatorColor != null) {
            g.setColor(separatorColor);
            g.drawLine(0, 0, getWidth(), 0);
        } else {
            super.paint(g);
        }
    }


    private static Color getSeparatorColor() {
        return UIUtils.getDarkerLine(new JPanel().getBackground(), 0.8f);
    }

}
