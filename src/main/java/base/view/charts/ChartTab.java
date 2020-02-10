package base.view.charts;

import base.Editor;

import javax.swing.*;
import java.awt.*;

public class ChartTab extends JPanel {

    private String name;
    private Editor editor;

    public ChartTab(Editor editor, String name) {
        super(new BorderLayout());
        this.editor = editor;
        this.name = name;
        add(new JButton(name.replace("_", " ") + ". Тут могла бути ваша реклама"), BorderLayout.CENTER);
    }

    public String getPrettyName() {
        return name;
    }

}
