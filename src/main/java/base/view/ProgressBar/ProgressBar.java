package base.view.ProgressBar;

import base.Editor;

import javax.swing.*;
import java.awt.event.WindowEvent;

public class ProgressBar extends JDialog {
    private JPanel rootPanel;
    private JProgressBar progressBar1;
    private JLabel label;
    private String[][] text = {{"Завершення експерименту. Зачекайте хвильку...", "Завершення експерименту"},
                                {"Завантаження експерименту. Зачекайте хвильку...", "Завантаження експерименту"}};

    public ProgressBar(Editor editor, int type) {
        super(editor);
        if (type < 0 || type > text.length - 1) {
            type = 0;
        }
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle(text[type][1]);
        label.setText(text[type][0]);
        add(rootPanel);
        progressBar1.setIndeterminate(true);
        setModal(true);
        setResizable(false);
        pack();
    }

    public void closeProgressBar() {
        setVisible(false);
    }

}
