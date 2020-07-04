package base.view.Settings;

import base.Editor;

import javax.swing.*;

public abstract class MainForm extends JDialog {
    private final Editor editor;
    private JPanel rootPanel;
    private JButton cancel;
    private JButton ok;
    private JPanel viewPanel;

    public MainForm(Editor editor, String title, boolean modal, boolean resizable) {
        super(editor);
        this.editor = editor;
        setTitle(title);
        add(rootPanel);
        setModal(modal);
        setResizable(resizable);
        ok.addActionListener(e -> okAction());
        cancel.addActionListener(e -> cancelAction());
    }

    public abstract void cancelAction();

    public abstract void okAction();

    protected void initButtons(String cancel, String ok) {
        this.cancel.setText(cancel);
        this.ok.setText(ok);
    }

    public Editor getEditor() {
        return editor;
    }

    public JButton getCancel() {
        return cancel;
    }

    public JButton getOk() {
        return ok;
    }

    protected void setViewPanel(JPanel viewPanel) {
        this.viewPanel.add(viewPanel);
        pack();
    }

}
