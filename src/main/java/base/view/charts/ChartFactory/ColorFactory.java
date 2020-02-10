package base.view.charts.ChartFactory;

import java.awt.Color;

/**
 * Utility class to access colors predefined for VisualVM.
 *
 * @author Jiri Sedlacek
 */
final class ColorFactory {

    private static final Color[] PREDEFINED_COLORS = new Color[] {
            new Color(241, 154,  42),
            new Color( 32, 171, 217),
            new Color(144,  97, 207),
            new Color(158, 156,   0)
    };

    private static final Color[][] PREDEFINED_GRADIENTS = new Color[][] {
            new Color[] { new Color(245, 204, 152), new Color(255, 243, 226) },
            new Color[] { new Color(151, 223, 246), new Color(227, 248, 255) },
            new Color[] { new Color(200, 163, 248), new Color(242, 232, 255) },
            new Color[] { new Color(212, 211, 131), new Color(244, 243, 217) }
    };


    /**
     * Returns number of colors predefined for VisualVM charts.
     * Always contains at least 4 colors.
     *
     * @return number of colors predefined for VisualVM charts
     */
    public static int getPredefinedColorsCount() {
        return PREDEFINED_COLORS.length;
    }

    /**
     * Returns a color predefined for VisualVM charts.
     *
     * @param index index of the predefined color
     * @return color predefined for VisualVM charts
     */
    public static Color getPredefinedColor(int index) {
        return PREDEFINED_COLORS[index];
    }


    /**
     * Returns number of color pairs predefined for VisualVM charts gradients.
     * Always contains at least 4 color pairs.
     *
     * @return number of color pairs predefined for VisualVM charts gradients
     */
    public static int getPredefinedGradientsCount() {
        return PREDEFINED_GRADIENTS.length;
    }

    /**
     * Returns a color pair predefined for VisualVM charts gradients.
     *
     * @param index index of the predefined color pair
     * @return color pair predefined for VisualVM charts gradients
     */
    public static Color[] getPredefinedGradient(int index) {
        return PREDEFINED_GRADIENTS[index];
    }

}
