package base.view.ProgressBar;

import base.Editor;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class ProgressBar extends JDialog {
    private JPanel rootPanel;
    private JProgressBar progressBar1;
    private JLabel label;

    public ProgressBar(Editor editor) {
        super(editor);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setTitle("Завершення експерименту");
        add(rootPanel);
        progressBar1.setIndeterminate(true);
        setModal(true);
        setResizable(false);
        pack();
    }

    public void closeProgressBar() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) { }
        setVisible(false);
    }

}
