package base.view.charts.ChartFactory.xy;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import org.netbeans.lib.profiler.charts.ChartConfigurationListener;
import org.netbeans.lib.profiler.charts.PaintersModel;
import org.netbeans.lib.profiler.charts.Timeline;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYChart;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItemsModel;
import org.netbeans.modules.profiler.api.icons.GeneralIcons;
import org.netbeans.modules.profiler.api.icons.Icons;

/**
 *
 * @author Jiri Sedlacek
 */
class SimpleXYChart extends SynchronousXYChart {

    private static final Icon ZOOM_IN_ICON = Icons.getIcon(GeneralIcons.ZOOM_IN);
    private static final Icon ZOOM_OUT_ICON = Icons.getIcon(GeneralIcons.ZOOM_OUT);
    private static final Icon FIXED_SCALE_ICON = Icons.getIcon(GeneralIcons.ZOOM);
    private static final Icon SCALE_TO_FIT_ICON = Icons.getIcon(GeneralIcons.SCALE_TO_FIT);

    private JScrollBar scroller;

    private ZoomInAction zoomInAction;
    private ZoomOutAction zoomOutAction;
    private ToggleViewAction toggleViewAction;

    private ChartConfigurationListener listener;


    SimpleXYChart(SynchronousXYItemsModel itemsModel, PaintersModel paintersModel) {
        super(itemsModel, paintersModel);
        setMousePanningEnabled(false);
    }


    void setZoomingEnabled(boolean zooming) {
        if (isZoomingEnabled() == zooming) return;

        if (zooming) enableZooming();
        else disableZooming();
    }

    boolean isZoomingEnabled() {
        return listener != null;
    }


    JScrollBar getScroller() {
        return scroller;
    }

    Action[] getActions() {
        return toggleViewAction == null ? new Action[0] :
                new Action[] { toggleViewAction, zoomInAction, zoomOutAction };
    }


    private void enableZooming() {
        scroller = new JScrollBar(JScrollBar.HORIZONTAL);
        attachHorizontalScrollBar(scroller);

        zoomInAction = new ZoomInAction();
        zoomOutAction = new ZoomOutAction();
        toggleViewAction = new ToggleViewAction();

        listener = new VisibleBoundsListener();
        addConfigurationListener(listener);
    }

    private void disableZooming() {
        removeConfigurationListener(listener);
        listener = null;

        detachHorizontalScrollBar();
        scroller = null;

        zoomInAction = null;
        zoomOutAction = null;
        toggleViewAction = null;
    }


    private class ZoomInAction extends AbstractAction {

//        private static final int ONE_SECOND_WIDTH_THRESHOLD = 200;

        public ZoomInAction() {
            super();

            putValue(SHORT_DESCRIPTION, "Zoom In (Mouse Wheel)"); // NOI18N
            putValue(SMALL_ICON, ZOOM_IN_ICON);

            updateAction();
        }

        public void actionPerformed(ActionEvent e) {
            boolean followsWidth = currentlyFollowingDataWidth();
            zoom(getWidth() / 2, getHeight() / 2, 2d);
            if (followsWidth) setOffset(getMaxOffsetX(), getOffsetY());

            repaintDirty();
        }

        private void updateAction() {
            Timeline timeline = ((SynchronousXYItemsModel)getItemsModel()).getTimeline();
            setEnabled(timeline.getTimestampsCount() > 1 && !fitsWidth() /*&&
                       getViewWidth(1000) < ONE_SECOND_WIDTH_THRESHOLD*/);
            // #165429 - don't disable zoom icons until mouse zoom is also disabled
        }

    }

    private class ZoomOutAction extends AbstractAction {

//        private static final float USED_CHART_WIDTH_THRESHOLD = 0.33f;

        public ZoomOutAction() {
            super();

            putValue(SHORT_DESCRIPTION, "Zoom Out (Mouse Wheel)");
            putValue(SMALL_ICON, ZOOM_OUT_ICON);

            updateAction();
        }

        public void actionPerformed(ActionEvent e) {
            boolean followsWidth = currentlyFollowingDataWidth();
            zoom(getWidth() / 2, getHeight() / 2, 0.5d);
            if (followsWidth) setOffset(getMaxOffsetX(), getOffsetY());

            repaintDirty();
        }

        private void updateAction() {
            Timeline timeline = ((SynchronousXYItemsModel)getItemsModel()).getTimeline();
            setEnabled(timeline.getTimestampsCount() > 1 && !fitsWidth() /*&&
                       getContentsWidth() > getWidth() * USED_CHART_WIDTH_THRESHOLD*/);
            // #165429 - don't disable zoom icons until mouse zoom is also disabled
        }

    }

    private class ToggleViewAction extends AbstractAction {

        private long origOffsetX  = -1;
        private double origScaleX = -1;

        public ToggleViewAction() {
            super();
            updateAction();
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isMiddleMouseButton(e))
                        actionPerformed(null);
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            boolean fitsWidth = fitsWidth();

            if (!fitsWidth) {
                origOffsetX = getOffsetX();
                if (tracksDataWidth() && origOffsetX == getMaxOffsetX())
                    origOffsetX = Long.MAX_VALUE;
                origScaleX  = getScaleX();
            }

            setFitsWidth(!fitsWidth);

            if (fitsWidth && origOffsetX != -1 && origScaleX != -1) {
                setScale(origScaleX, getScaleY());
                setOffset(origOffsetX, getOffsetY());
            }

            updateAction();
            if (zoomInAction != null) zoomInAction.updateAction();
            if (zoomOutAction != null) zoomOutAction.updateAction();

            repaintDirty();

        }

        private void updateAction() {
            boolean fitsWidth = fitsWidth();
            Icon icon = fitsWidth ? FIXED_SCALE_ICON : SCALE_TO_FIT_ICON;
            String name = fitsWidth ? "Fixed Scale (Mouse Wheel Click)" : "Scale To Fit (Mouse Wheel Click)";
            putValue(SHORT_DESCRIPTION, name);
            putValue(SMALL_ICON, icon);

            if (scroller != null) scroller.setVisible(!fitsWidth);
        }

    }


    private class VisibleBoundsListener extends ChartConfigurationListener.Adapter {

        public void dataBoundsChanged(long dataOffsetX, long dataOffsetY,
                                      long dataWidth, long dataHeight,
                                      long oldDataOffsetX, long oldDataOffsetY,
                                      long oldDataWidth, long oldDataHeight) {

            if (zoomInAction != null) zoomInAction.updateAction();
            if (zoomOutAction != null) zoomOutAction.updateAction();
        }

        public void scaleChanged(double oldScaleX, double oldScaleY,
                                 double newScaleX, double newScaleY) {

            if (zoomInAction != null) zoomInAction.updateAction();
            if (zoomOutAction != null) zoomOutAction.updateAction();
        }
    }

}
