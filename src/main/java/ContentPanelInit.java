import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ContentPanelInit extends JPanel implements ItemListener {
    private JButton initButton, searchButton, renameButton, changeIdButton;
    private ResourceBundle bundle;
    private JTextField textField;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JCheckBox checksum;
    private JLabel percentLabel, startLabel, endLabel;
    private JComboBox<String> devicesComboBox, startComboBox, endComboBox;

    public ContentPanelInit(ResourceBundle bundle) {
        this.bundle = bundle;
        setLayout(new GridBagLayout());
        textField = new JTextField(40);
        initButton = new JButton("Ініціалізувати Порт");
        searchButton = new JButton("Знайти модулі");
        renameButton = new JButton("Перейменувати");
        changeIdButton = new JButton("Змінити ID модуля");
        textArea = new JTextArea();
        textArea.setRows(16);
        textArea.setColumns(40);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        checksum = new JCheckBox("Контрольна сума", false);
        devicesComboBox = new JComboBox<>();
        startComboBox = new JComboBox<>();
        endComboBox = new JComboBox<>();
        percentLabel = new JLabel();
        startLabel = new JLabel("Задати діапазон пошуку");
        startComboBox.addItemListener(this);
        init();
        for (short i = 0; i < 255; i++) {
            startComboBox.addItem(String.format("%02X", i));
        }
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
        add(startLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(startComboBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(endComboBox, new GridBagConstraints(2, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(searchButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(changeIdButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(renameButton, new GridBagConstraints(2, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(devicesComboBox, new GridBagConstraints(0, 2, 4, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(scrollPane, new GridBagConstraints(0, 3, 4, 1, 1, 1,
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
            devicesComboBox.addItem("ID:" + str[0]);
        }
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

    public int getStart() {
        return startComboBox.getSelectedIndex();
    }

    public int getEnd() {
        return startComboBox.getSelectedIndex() + endComboBox.getSelectedIndex() + 1;
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.DESELECTED) return;
        if(event.getSource() == startComboBox){
            endComboBox.removeAllItems();
            for (int i = startComboBox.getSelectedIndex(); i < 256; i++) {
                endComboBox.addItem(String.format("%02X", i));
            }
        }
    }
}
