package base.view;

import base.Editor;

import javax.swing.*;
import java.awt.*;

public class DiagramTab extends JPanel {

    private String name;
    private Editor editor;

    public DiagramTab(Editor editor, String name) {
        super(new BorderLayout());
        this.editor = editor;
        this.name = name;
        add(new JButton(name), BorderLayout.CENTER);
    }

    public String getPrettyName() {
        return name;
    }
}
