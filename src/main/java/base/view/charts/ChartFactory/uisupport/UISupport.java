package base.view.charts.ChartFactory.uisupport;

import java.awt.Color;
import org.netbeans.lib.profiler.ui.UIUtils;

/**
 * Various UI methods published from org.netbeans.lib.profiler.ui.UIUtils to be
 * used in VisualVM tool and plugins.
 *
 * @author Jiri Sedlacek
 */
public class UISupport {

    /** Returns default background of tables & textcomponents */
    public static Color getDefaultBackground() {
        return UIUtils.getProfilerResultsBackground();
    }

    /** Determines if current L&F is AquaLookAndFeel */
    public static boolean isAquaLookAndFeel() {
        return UIUtils.isAquaLookAndFeel();
    }

    /** Determines if current L&F is GTKLookAndFeel */
    public static boolean isGTKLookAndFeel() {
        return UIUtils.isGTKLookAndFeel();
    }

    /** Determines if current L&F is Nimbus */
    public static boolean isNimbusLookAndFeel() {
        return UIUtils.isNimbusLookAndFeel();
    }

    /** Determines if current L&F is GTK using Nimbus theme */
    public static boolean isNimbusGTKTheme() {
        return UIUtils.isNimbusGTKTheme();
    }

    /** Determines if current L&F is Nimbus or GTK with Nimbus theme*/
    public static boolean isNimbus() {
        return UIUtils.isNimbus();
    }

    /** Determines if current L&F is MetalLookAndFeel */
    public static boolean isMetalLookAndFeel() {
        return UIUtils.isMetalLookAndFeel();
    }

    /** Determines if current L&F is Windows Classic LookAndFeel */
    public static boolean isWindowsClassicLookAndFeel() {
        return UIUtils.isWindowsClassicLookAndFeel();
    }

    /** Determines if current L&F is WindowsLookAndFeel */
    public static boolean isWindowsLookAndFeel() {
        return UIUtils.isWindowsLookAndFeel();
    }

    /** Determines if current L&F is Windows XP LookAndFeel */
    public static boolean isWindowsXPLookAndFeel() {
        return UIUtils.isWindowsXPLookAndFeel();
    }

    public static void runInEventDispatchThread(final Runnable r) {
        UIUtils.runInEventDispatchThread(r);
    }

    public static void runInEventDispatchThreadAndWait(final Runnable r) {
        UIUtils.runInEventDispatchThreadAndWait(r);
    }

}
