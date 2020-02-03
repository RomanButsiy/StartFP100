package base.view;

import base.PreferencesData;
import libraries.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static libraries.Theme.scale;

public class EditorLineStatus extends JComponent {

    private static final int RESIZE_IMAGE_SIZE = scale(20);

    Color foreground;
    Color background;
    Color messageForeground;

    Font font;
    int height;

    String text = "";
    String port = "";

    public EditorLineStatus() {
        background = Theme.getColor("linestatus.bgcolor");
        font = Theme.getFont("linestatus.font");
        foreground = Theme.getColor("linestatus.color");
        height = Theme.getInteger("linestatus.height");
    }

    public void paintComponent(Graphics graphics) {
        Graphics2D g = Theme.setupGraphics2D(graphics);
        if (port == null) {
            setPort(PreferencesData.get("serial.port", ""));
        }
        g.setColor(background);
        Dimension size = getSize();
        g.fillRect(0, 0, size.width, size.height);
        g.setFont(font);
        g.setColor(foreground);
        int baseline = (size.height + g.getFontMetrics().getAscent()) / 2;
        g.drawString(text, scale(6), baseline);
        g.setColor(messageForeground);
        String statusText;
        if (port != null && !port.isEmpty()) {
            statusText = "Вибрано порт " + port;
        } else {
            statusText = "Порт не вибрано";
        }
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(statusText, null);
        g.drawString(statusText, size.width - (int) bounds.getWidth() - RESIZE_IMAGE_SIZE, baseline);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Dimension getPreferredSize() {
        return scale(new Dimension(300, height));
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return scale(new Dimension(3000, height));
    }

}
