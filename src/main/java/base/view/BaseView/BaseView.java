package base.view.BaseView;

import base.Editor;

import javax.swing.*;
import java.awt.event.ActionListener;

public class BaseView extends JDialog {
    private final Editor editor;
    private JPanel rootPanel;
    private JPanel viewPanel;
    private JButton buttonLeftPL;
    private JButton buttonRightPR;
    private JButton buttonCenterPR;
    private JButton buttonLeftPR;

    public BaseView(Editor editor, String title, boolean modal, boolean resizable) {
        super(editor);
        this.editor = editor;
        setTitle(title);
        add(rootPanel);
        setModal(modal);
        setResizable(resizable);
    }

    protected void initButtons(String button1, String button2, String button3, String button4) {
        buttonLeftPL.setText(button1);
        buttonLeftPR.setText(button2);
        buttonCenterPR.setText(button3);
        buttonRightPR.setText(button4);
    }

    protected void initButtons(String button1, String button2, String button4) {
        buttonCenterPR.setVisible(false);
        initButtons(button1, button2, "", button4);
    }

    public void buttonLeftPLListener(ActionListener listener) {
        buttonLeftPL.addActionListener(listener);
    }

    public void buttonRightPRListener(ActionListener listener) {
        buttonRightPR.addActionListener(listener);
    }

    public void buttonCenterPRListener(ActionListener listener) {
        buttonCenterPR.addActionListener(listener);
    }

    public void buttonLeftPRListener(ActionListener listener) {
        buttonLeftPR.addActionListener(listener);
    }

    public void setViewPanel(JPanel viewPanel) {
        this.viewPanel.add(viewPanel);
        pack();
    }

    public JButton getButtonLeftPL() {
        return buttonLeftPL;
    }

    public JButton getButtonRightPR() {
        return buttonRightPR;
    }

    public JButton getButtonCenterPR() {
        return buttonCenterPR;
    }

    public JButton getButtonLeftPR() {
        return buttonLeftPR;
    }

    public Editor getEditor() {
        return editor;
    }

}
