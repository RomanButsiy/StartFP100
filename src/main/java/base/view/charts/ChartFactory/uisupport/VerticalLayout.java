package base.view.charts.ChartFactory.uisupport;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.Box;

/**
 *
 * @author Jiri Sedlacek
 */
public final class VerticalLayout implements LayoutManager {

    private final boolean proportionalWidth;
    private final int vGap;


    public VerticalLayout(boolean proportionalWidth) {
        this(proportionalWidth, 0);
    }

    public VerticalLayout(boolean proportionalWidth, int vGap) {
        this.proportionalWidth = proportionalWidth;
        this.vGap = vGap;
    }


    public void layoutContainer(final Container parent) {
        final Insets insets = parent.getInsets();
        final int posX = insets.left;
        int posY = insets.top;
        final int width = parent.getWidth() - insets.left - insets.right;

        for (Component comp : parent.getComponents()) {
            if (comp.isVisible()) {
                Dimension pref = comp.getPreferredSize();
                if (proportionalWidth) {
                    int w = Math.min(pref.width, width);
                    int o = (width - w) / 2;
                    comp.setBounds(posX, posY + o, w, pref.height);
                } else {
                    comp.setBounds(posX, posY, width, pref.height);
                }
                pref.height += vGap;
                posY += pref.height;
            }
        }
    }

    public Dimension minimumLayoutSize(final Container parent) {
        final Insets insets = parent.getInsets();
        final Dimension d = new Dimension(insets.left + insets.right,
                insets.top + insets.bottom);
        int maxWidth = 0;
        int visibleCount = 0;

        for (Component comp : parent.getComponents()) {
            if (comp.isVisible() && !(comp instanceof Box.Filler)) {
                final Dimension size = comp.getPreferredSize();
                maxWidth = Math.max(maxWidth, size.width);
                d.height += size.height;
                visibleCount++;
            }
        }

        d.height += (visibleCount - 1) * vGap;
        d.width += maxWidth;

        return d;
    }

    public Dimension preferredLayoutSize(final Container parent) {
        final Insets insets = parent.getInsets();
        final Dimension d = new Dimension(insets.left + insets.right,
                insets.top + insets.bottom);
        int maxWidth = 0;
        int visibleCount = 0;

        for (Component comp : parent.getComponents()) {
            if (comp.isVisible()) {
                final Dimension size = comp.getPreferredSize();
                maxWidth = Math.max(maxWidth, size.width);
                d.height += size.height;
                visibleCount++;
            }
        }

        d.height += (visibleCount - 1) * vGap;
        d.width += maxWidth;

        return d;
    }


    public void addLayoutComponent(final String name, final Component comp) {}

    public void removeLayoutComponent(final Component comp) {}

}
