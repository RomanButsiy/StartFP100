package base.view;

import base.Editor;
import base.processing.ExperimentController;
import libraries.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static libraries.Theme.scale;

public class EditorHeader  extends JComponent {

    static Color backgroundColor;
    static Color[] textColor = new Color[2];

    Editor editor;

    int[] tabLeft;
    int[] tabRight;

    Font font;
    FontMetrics metrics;
    int fontAscent;

    static final String[] STATUS = { "unsel", "sel" };
    static final int UNSELECTED = 0;
    static final int SELECTED = 1;

    static final String[] WHERE = { "left", "mid", "right" };
    static final int LEFT = 0;
    static final int MIDDLE = 1;
    static final int RIGHT = 2;

    static final int PIECE_WIDTH = scale(4);
    static final int PIECE_HEIGHT = scale(33);

    static final int GRID_SIZE = 33;

    static Image[][] pieces;

    Image offscreen;
    int sizeW, sizeH;
    int imageW, imageH;


    public EditorHeader(Editor editor) {
        this.editor = editor;

        if (pieces == null) {
            pieces = new Image[STATUS.length][WHERE.length];
            for (int i = 0; i < STATUS.length; i++) {
                for (int j = 0; j < WHERE.length; j++) {
                    String path = "tab-" + STATUS[i] + "-" + WHERE[j];
                    pieces[i][j] = Theme.getThemeImage(path, this, PIECE_WIDTH,
                            PIECE_HEIGHT);
                }
            }
        }
        if (backgroundColor == null) {
            backgroundColor =
                    Theme.getColor("header.bgcolor");
            textColor[SELECTED] =
                    Theme.getColor("header.text.selected.color");
            textColor[UNSELECTED] =
                    Theme.getColor("header.text.unselected.color");
        }
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int numTabs = editor.getTabs().size();
                for (int i = 0; i < numTabs; i++) {
                    if ((x > tabLeft[i]) && (x < tabRight[i])) {
                        editor.selectTab(i);
                        repaint();
                    }
                }
            }
        });
    }

    public void paintComponent(Graphics screen) {
        if (screen == null) return;
        ExperimentController experiment = editor.getExperimentController();
        if (experiment == null) return;
        Dimension size = getSize();
        if ((size.width != sizeW) || (size.height != sizeH)) {
            if ((size.width > imageW) || (size.height > imageH)) {
                offscreen = null;
            } else {
                // who cares, just resize
                sizeW = size.width;
                sizeH = size.height;
            }
        }
        if (offscreen == null) {
            sizeW = size.width;
            sizeH = size.height;
            imageW = sizeW;
            imageH = sizeH;
            offscreen = createImage(imageW, imageH);
        }
        Graphics2D g = Theme.setupGraphics2D(offscreen.getGraphics());
        if (font == null) {
            font = Theme.getFont("header.text.font");
        }
        g.setFont(font);
        metrics = g.getFontMetrics();
        fontAscent = metrics.getAscent();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, imageW, imageH);
        List<DiagramTab> tabs = editor.getTabs();
        int codeCount = tabs.size();
        if ((tabLeft == null) || (tabLeft.length < codeCount)) {
            tabLeft = new int[codeCount];
            tabRight = new int[codeCount];
        }
        int x = scale(6);
        int i = 0;
        for (DiagramTab tab : tabs) {
            String diagramName = " " + tab.getPrettyName() + " ";
            int textWidth = (int) font.getStringBounds(diagramName, g.getFontRenderContext()).getWidth();
            int pieceCount = 2 + (textWidth / PIECE_WIDTH);
            int pieceWidth = pieceCount * PIECE_WIDTH;
            int state = (i == editor.getCurrentTabIndex()) ? SELECTED : UNSELECTED;
            g.drawImage(pieces[state][LEFT], x, 0, null);
            x += PIECE_WIDTH;
            int contentLeft = x;
            tabLeft[i] = x;
            for (int j = 0; j < pieceCount; j++) {
                g.drawImage(pieces[state][MIDDLE], x, 0, null);
                x += PIECE_WIDTH;
            }
            tabRight[i] = x;
            int textLeft = contentLeft + (pieceWidth - textWidth) / 2;
            g.setColor(textColor[state]);
            int baseline = (sizeH + fontAscent) / 2;
            g.drawString(diagramName, textLeft, baseline);
            g.drawImage(pieces[state][RIGHT], x, 0, null);
            x += PIECE_WIDTH - 1;
            i++;
        }
        screen.drawImage(offscreen, 0, 0, null);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }


    public Dimension getMinimumSize() {
        return scale(new Dimension(300, GRID_SIZE));
    }


    public Dimension getMaximumSize() {
        return scale(new Dimension(30000, GRID_SIZE));
    }

}
