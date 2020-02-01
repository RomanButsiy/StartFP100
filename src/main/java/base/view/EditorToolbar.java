package base.view;

import base.Editor;
import libraries.Theme;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.item.view.TouchBarButton;

import static libraries.Theme.scale;

public class EditorToolbar extends JComponent implements MouseInputListener, KeyEventDispatcher {

    private static final String[] title = { "Розпочати експеримент", "Зупинити експеримент", "Налаштування експерименту" };

    private static final int BUTTON_COUNT = title.length;
    /**
     * Width of each toolbar button.
     */
    private static final int BUTTON_WIDTH = scale(27);
    /**
     * Height of each toolbar button.
     */
    private static final int BUTTON_HEIGHT = scale(32);
    /**
     * The amount of space between groups of buttons on the toolbar.
     */
    private static final int BUTTON_GAP = scale(5);
    /**
     * Size of the button image being chopped up.
     */
    private static final int BUTTON_IMAGE_SIZE = scale(33);


    private static final int RUN = 0;
    private static final int STOP = 1;

    private static final int SETTINGS = 2;

    private static final int INACTIVE = 0;
    private static final int ROLLOVER = 1;
    private static final int ACTIVE = 2;

    private final Editor editor;

    private Image offscreen;
    private int width;
    private int height;

    private final Color bgColor;

    private static Image[][] buttonImages;
    private int currentRollover;

    private int buttonCount;
    private int[] state = new int[BUTTON_COUNT];
    private Image[] stateImage;
    private final int[] which; // mapping indices to implementation

    private int[] x1;
    private int[] x2;
    private int y1;
    private int y2;

    private final Font statusFont;
    private final Color statusColor;

    public EditorToolbar(Editor editor) {
        this.editor = editor;
        buttonCount = 0;
        which = new int[BUTTON_COUNT];
        which[buttonCount++] = RUN;
        which[buttonCount++] = STOP;
        which[buttonCount++] = SETTINGS;
        currentRollover = -1;
        bgColor = Theme.getColor("buttons.bgcolor");
        statusFont = Theme.getFont("buttons.status.font");
        statusColor = Theme.getColor("buttons.status.color");
        addMouseListener(this);
        addMouseMotionListener(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public void paintComponent(Graphics screen) {
        if (buttonImages == null) {
            loadButtons();
        }
        if (stateImage == null) {
            state = new int[buttonCount];
            stateImage = new Image[buttonCount];
            for (int i = 0; i < buttonCount; i++) {
                setState(i, INACTIVE, false);
            }
            y1 = 0;
            y2 = BUTTON_HEIGHT;
            x1 = new int[buttonCount];
            x2 = new int[buttonCount];
        }
        Dimension size = getSize();
        if ((offscreen == null) ||
                (size.width != width) || (size.height != height)) {
            offscreen = createImage(size.width, size.height);
            width = size.width;
            height = size.height;
            int offsetX = 3;
            for (int i = 0; i < buttonCount; i++) {
                x1[i] = offsetX;
                if (i == 2 || i == 6) x1[i] += BUTTON_GAP;
                x2[i] = x1[i] + BUTTON_WIDTH;
                offsetX = x2[i];
            }
            x1[SETTINGS] = width - BUTTON_WIDTH - 14;
            x2[SETTINGS] = width - 14;
        }
        Graphics2D g = Theme.setupGraphics2D(offscreen.getGraphics());
        g.setColor(bgColor); //getBackground());
        g.fillRect(0, 0, width, height);
        for (int i = 0; i < buttonCount; i++) {
            g.drawImage(stateImage[i], x1[i], y1, null);
        }
        g.setColor(statusColor);
        g.setFont(statusFont);
        if (currentRollover != -1) {
            int statusY = (BUTTON_HEIGHT + g.getFontMetrics().getAscent()) / 2;
            if (currentRollover != SETTINGS)
                g.drawString(title[currentRollover], (buttonCount - 1) * BUTTON_WIDTH + 3 * BUTTON_GAP, statusY);
            else {
                int statusX = x1[SETTINGS] - BUTTON_GAP;
                statusX -= g.getFontMetrics().stringWidth(title[currentRollover]);
                g.drawString(title[currentRollover], statusX, statusY);
            }
        }
        screen.drawImage(offscreen, 0, 0, null);
        if (!isEnabled()) {
            screen.setColor(new Color(0, 0, 0, 100));
            screen.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (!isEnabled())
            return;
        final int x = mouseEvent.getX();
        final int y = mouseEvent.getY();
        int sel = findSelection(x, y);
        if (sel == -1) return;
        currentRollover = -1;
        handleSelectionPressed(sel);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        handleMouse(mouseEvent);
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        handleMouse(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        if (!isEnabled())
            return;
        if (state == null) return;
        handleMouse(mouseEvent);
    }

    private void handleMouse(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (currentRollover != -1) {
            if ((x > x1[currentRollover]) && (y > y1) &&
                    (x < x2[currentRollover]) && (y < y2)) {
                return;

            } else {
                setState(currentRollover, INACTIVE, true);
                currentRollover = -1;
            }
        }
        int sel = findSelection(x, y);
        if (sel == -1) return;

        if (state[sel] != ACTIVE) {
            setState(sel, ROLLOVER, true);
            currentRollover = sel;
        }
    }

    private int findSelection(int x, int y) {
        if ((x1 == null) || (x2 == null)) return -1;

        for (int i = 0; i < buttonCount; i++) {
            if ((y > y1) && (x > x1[i]) &&
                    (y < y2) && (x < x2[i])) {
                return i;
            }
        }
        return -1;
    }

    private void loadButtons() {
        Image allButtons = Theme.getThemeImage("buttons", this,
                BUTTON_IMAGE_SIZE * BUTTON_COUNT,
                BUTTON_IMAGE_SIZE * 3);
        buttonImages = new Image[BUTTON_COUNT][3];
        for (int i = 0; i < BUTTON_COUNT; i++) {
            for (int state = 0; state < 3; state++) {
                Image image = createImage(BUTTON_WIDTH, BUTTON_HEIGHT);
                Graphics g = image.getGraphics();
                g.setColor(bgColor);
                g.fillRect(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
                int offset = (BUTTON_IMAGE_SIZE - BUTTON_WIDTH) / 2;
                g.drawImage(allButtons, -(i * BUTTON_IMAGE_SIZE) - offset,
                        (-2 + state) * BUTTON_IMAGE_SIZE, null);
                buttonImages[i][state] = image;
            }
        }
    }

    private void setState(int slot, int newState, boolean updateAfter) {
        state[slot] = newState;
        stateImage[slot] = buttonImages[which[slot]][newState];
        if (updateAfter) {
            repaint();
        }
    }

    private void handleSelectionPressed(int sel) {
        switch (sel) {
            case RUN:
                editor.handleRun();
                break;
            case STOP:
                editor.handleStop();
                break;
            case SETTINGS:
                editor.handleExperimentSettings();
                break;
            default:
                break;
        }
    }

    private void activate(int what) {
        if (buttonImages != null) {
            setState(what, ACTIVE, true);
        }
    }

    private void deactivate(int what) {
        if (buttonImages != null) {
            setState(what, INACTIVE, true);
        }
    }

    public void activateRun() {
        activate(RUN);
    }

    public void activateStop() {
        activate(STOP);
    }

    public void activateSettings() {
        activate(SETTINGS);
    }

    public void deactivateRun() {
        deactivate(RUN);
    }

    public void deactivateStop() {
        deactivate(STOP);
    }

    public void deactivateSettings() {
        deactivate(SETTINGS);
    }

}
