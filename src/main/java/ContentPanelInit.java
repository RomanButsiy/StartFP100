import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ContentPanelInit extends JPanel{
    private JButton initButton, searchButton, renameButton, changeIdButton, sendButton;
    private ResourceBundle bundle;
    private JTextField textField;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JCheckBox checksum;
    private JLabel percentLabel;
    private JComboBox<String> devicesComboBox;

    public ContentPanelInit(ResourceBundle bundle) {
        this.bundle = bundle;
        setLayout(new GridBagLayout());
        textField = new JTextField(10);
        sendButton = new JButton(bundle.getString("sendButton"));
        initButton = new JButton("Ініціалізувати Порт");
        searchButton = new JButton("Знайти модулі");
        renameButton = new JButton("NULL");
        changeIdButton = new JButton("NULL");
        textArea = new JTextArea();
        textArea.setRows(16);
        textArea.setColumns(40);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        checksum = new JCheckBox("Контрольна сума", false);
        devicesComboBox = new JComboBox<>();
        percentLabel = new JLabel();
        init();
    }

    public void onChecksumCommand(ActionListener listener) {
        checksum.addActionListener(listener);
    }

    public void onInitCommand(ActionListener listener) {
        initButton.addActionListener(listener);
    }

    public void onSearchCommand(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void onSendCommand(ActionListener listener) {
        textField.addActionListener(listener);
        sendButton.addActionListener(listener);
    }

    public void onTextFieldKey(KeyListener listener) {
        textField.addKeyListener(listener);
    }

    public String getTextField() {
        return textField.getText();
    }

    public void setTextField(String text) {
        textField.setText(text);
    }

    private void init() {
        Insets insets = new Insets(2, 2, 2, 2);
        JPopupMenu menu = new JPopupMenu();
        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, bundle.getString("popupCut"));
        menu.add(cut);
        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, bundle.getString("popupCopy"));
        menu.add(copy);
        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, bundle.getString("popupPaste"));
        menu.add(paste);
        textField.setComponentPopupMenu(menu);
        add(searchButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(changeIdButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(renameButton, new GridBagConstraints(2, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(devicesComboBox, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(textField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(sendButton, new GridBagConstraints(2, 2, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(scrollPane, new GridBagConstraints(0, 3, 3, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(initButton, new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(checksum, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(percentLabel, new GridBagConstraints(2, 4, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    }

    public void setEnabledSearchButton(boolean set) {
        searchButton.setEnabled(set);
    }

    public void setTextPercentLabel(String str) {
        percentLabel.setText(str);
    }

    public void updateData(ArrayList<String[]> strList) {
        textArea.setText("");
        devicesComboBox.removeAllItems();
        for (String [] str : strList) {
            textArea.append("Ідентифікатор модуля: " + str[0] + " Ім'я модуля: " + str[1] + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            devicesComboBox.addItem("ID:" + str[0]);
        }
    }

    public void updateTextArea(String str) {
        textArea.append(str);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public boolean checksumIsSelected() {
        return checksum.isSelected();
    }

    protected void onEnableWindow(boolean enable) {
        textArea.setEnabled(enable);
        scrollPane.setEnabled(enable);
        textField.setEnabled(enable);
        sendButton.setEnabled(enable);
        renameButton.setEnabled(enable);
        changeIdButton.setEnabled(enable);
        devicesComboBox.setEnabled(enable);
    }

}
