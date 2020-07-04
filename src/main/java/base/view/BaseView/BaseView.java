package base.view.BaseView;

import base.Editor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public abstract class BaseView extends JDialog {
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
        buttonLeftPRListener(actionEvent -> okAction());
        buttonCenterPRListener(actionEvent -> closeAction_());
        buttonRightPRListener(actionEvent -> applyAction());
        buttonLeftPLListener(actionEvent -> leftButtonAction());
    }

    public abstract void leftButtonAction();

    public void okAction() {
        applyAction_();
        closeWindow_();
    }

    public void closeWindow_() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void applyAction_() {
        applyAction();
    }

    public abstract void applyAction();

    public void closeAction_() {
        closeAction();
        closeWindow_();
    }

    public abstract void closeAction();

    protected void initButtons(String button1, String button2, String button3, String button4) {
        buttonLeftPL.setText(button1);
        buttonLeftPR.setText(button2);
        buttonCenterPR.setText(button3);
        buttonRightPR.setText(button4);
    }

    protected void initButtons(String button1, String button2, String button3) {
        buttonRightPR.setVisible(false);
        initButtons(button1, button2, button3, "");
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

    protected void setViewPanel(JPanel viewPanel) {
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
