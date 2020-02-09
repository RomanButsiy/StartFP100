package base.view.ProgressBar;

import base.Editor;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class ProgressBar extends JDialog {
    private JPanel rootPanel;
    private JProgressBar progressBar1;

    public ProgressBar(Editor editor) {
        super(editor);
        //setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle("Завершення експерименту");
        add(rootPanel);
        progressBar1.setIndeterminate(true);
        setModal(true);
        setResizable(false);
        pack();
    }

    public void closeProgressBar() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

}
