import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class ContentPanelInit extends JPanel {
    private JButton initButton, searchButton, renameButton, changeIdButton;
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
        textField = new JTextField(40);
        initButton = new JButton("Ініціалізувати Порт");
        searchButton = new JButton("Знайти модулі");
        renameButton = new JButton("Перейменувати модуль");
        changeIdButton = new JButton("Змінити ID модуля");
        textArea = new JTextArea();
        textArea.setRows(16);
        textArea.setColumns(40);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        checksum = new JCheckBox("Контрольна сума", false);
        devicesComboBox = new JComboBox<String>();
        percentLabel = new JLabel();
        init();
    }

    public void onInitCommand(ActionListener listener) {
        initButton.addActionListener(listener);
    }

    public void onSearchCommand(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void onRenameCommand(ActionListener listener) {
        renameButton.addActionListener(listener);
    }

    public void onChangeIdCommand(ActionListener listener) {
        changeIdButton.addActionListener(listener);
    }


    private void init() {
        Insets insets = new Insets(2, 2, 2, 2);
        add(searchButton, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(changeIdButton, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(renameButton, new GridBagConstraints(2, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(devicesComboBox, new GridBagConstraints(0, 1, 3, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(scrollPane, new GridBagConstraints(0, 2, 3, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(initButton, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(checksum, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(percentLabel, new GridBagConstraints(2, 3, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    }

    public void setEnabledSearchButton(boolean set) {
        searchButton.setEnabled(set);
    }

    public void setTextPercentLabel(String str) {
        percentLabel.setText(str);
    }

    public void clearTextArea() {
        textArea.setText("");
    }

    public void appendStringTextArea(String str) {
        textArea.append(str);
    }

    public boolean checksumIsSelected() {
        return checksum.isSelected();
    }

    protected void onEnableWindow(boolean enable) {
        textArea.setEnabled(enable);
        scrollPane.setEnabled(enable);
        textField.setEnabled(enable);
        renameButton.setEnabled(enable);
        changeIdButton.setEnabled(enable);
        devicesComboBox.setEnabled(enable);
    }
}
