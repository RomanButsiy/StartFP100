package SettingsPanel;

import SerialDriver.SerialDriver;
import jssc.SerialPort;
import jssc.SerialPortException;

import javax.swing.*;
import SerialDriver.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import MenuBar.PortAndSpeed;
import libraries.I7000;

public class SettingsPanel extends JDialog implements ItemListener {

    private SerialDriver serialDriver;
    private SerialPort serialPort = null;
    private StringBuffer serialBuffer;
    private Boolean visible = true;
    private JButton searchButton, renameButton, changeIdButton;
    private ResourceBundle bundle;
    private Thread searchThread;
    private ArrayList<String []> devicesArrayList;
    private I7000 i7000;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel percentLabel, startLabel;
    private JComboBox<String> devicesComboBox, startComboBox, endComboBox;
    private JCheckBox checksum;

    public SettingsPanel(Frame frame, PortAndSpeed portAndSpeed, ResourceBundle bundle) {
        super(frame, bundle.getString("menuSettings") + " " + portAndSpeed.getPort(), true);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        serialDriver = new SerialDriver();
        serialBuffer = new StringBuffer();
        devicesArrayList = new ArrayList<>();
        serialDriver.initPort(portAndSpeed);
        searchThread = new Thread();
        i7000 = new I7000(false);
        if (serialDriver.isInit()) {
            serialPort = SerialDriver.getSerialPort();
            try {
                serialPort.addEventListener(new PortReader(serialPort, this::dataReadAction), SerialPort.MASK_RXCHAR);
            } catch (SerialPortException e) {
                portInitError(e.toString(), portAndSpeed.getPort());
            }
        } else {
            portInitError(serialDriver.getInitException(), portAndSpeed.getPort());
        }
        searchButton = new JButton("Знайти модулі");
        renameButton = new JButton("Перейменувати");
        changeIdButton = new JButton("Змінити ID модуля");
        textArea = new JTextArea();
        textArea.setRows(10);
        textArea.setColumns(40);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        devicesComboBox = new JComboBox<>();
        startComboBox = new JComboBox<>();
        endComboBox = new JComboBox<>();
        percentLabel = new JLabel();
        startLabel = new JLabel("Задати діапазон пошуку");
        checksum = new JCheckBox("Контрольна сума", false);
        startComboBox.addItemListener(this);
        init();
        for (short i = 0; i < 255; i++) {
            startComboBox.addItem(String.format("%02X", i));
        }
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (serialPort != null) {
                    try {
                        serialPort.closePort();
                    } catch (SerialPortException ex) {
                        ex.printStackTrace();
                    }
                }
                setVisible(false);
            }
        });
        searchButton.addActionListener(e -> {
            if (searchThread.isAlive()) {
                searchThread.stop();
                percentLabel.setText("Search stopped!!!");
            } else {
                searchThread = new Thread(this::SearchDevices);
                onEnableWindow(false);
                searchThread.start();
            }
        });
        renameButton.addActionListener(e -> {
            int index = devicesComboBox.getSelectedIndex();
            String resultString1 = JOptionPane.showInputDialog(null,
                    "Поточна назва пристрою " + devicesArrayList.get(index)[1] + ".\nВведіть нову назву пристрою",
                    "Перейменувати модуль", JOptionPane.QUESTION_MESSAGE);
            if (resultString1 == null || resultString1.equals("")) return;
            String [] newData = new String[]{ devicesArrayList.get(index)[0], resultString1 };
            serialDriver.write(i7000.setModuleName(newData));
            new Thread(this::waitResponse).start();
            devicesArrayList.set(index, newData);
            updateData(devicesArrayList);
        });
        onEnableWindow(false);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setVisible(visible);
    }

    private boolean waitResponse() {
        final int timeOut = 100;
        serialBuffer.setLength(0);
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime < timeOut)) {
            if (serialBuffer.indexOf("\r") != -1) {
                percentLabel.setText(String.valueOf(System.currentTimeMillis() - startTime));
                return true;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(null, "Час вийшов",
                "Помилка", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    private void SearchDevices() {
        final int SearchPause = 200;
        serialBuffer.setLength(0);
        StringBuilder str = new StringBuilder("$");
        for (int i = getStart(), j = getStart(), s = getEnd() - getStart(); i < getEnd(); i++) {
            str.append(String.format("%02X", i));
            str.append("M");
            if (checksumIsSelected()) {
                str.append(i7000.getCRC(str.toString().toCharArray()));
            }
            str.append("\r");
            serialDriver.write(str.toString());
            str.setLength(1);
            percentLabel.setText(100 * (i - j) / s + "%");
            try {
                Thread.sleep(SearchPause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        percentLabel.setText("100%");
        updateTextArea();
        searchThread.interrupt();
    }

    private void updateTextArea() {
        devicesArrayList.clear();
        if (serialBuffer.length() == 0) return;
        StringTokenizer tokenizer = new StringTokenizer(serialBuffer.toString(), "\r");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.length() < 3) {
                continue;
            }
            String [] str = new String[2];
            str[0] = token.substring(1, 3);
            str[1] = token.substring(3, token.length() - (checksumIsSelected() ? 2 : 0));
            devicesArrayList.add(str);
        }
        updateData(devicesArrayList);
        onEnableWindow(true);
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
        renameButton.setEnabled(enable);
        changeIdButton.setEnabled(enable);
        devicesComboBox.setEnabled(enable);
    }

    private void dataReadAction(String s) {
        serialBuffer.append(s);
    }

    private void portInitError(String err, String title) {
        JOptionPane.showMessageDialog(null, "Порт не вдалося ініціалізувати\n" + err,
                title, JOptionPane.ERROR_MESSAGE);
        visible = false;
    }

    public int getStart() {
        return startComboBox.getSelectedIndex();
    }

    public int getEnd() {
        return startComboBox.getSelectedIndex() + endComboBox.getSelectedIndex() + 1;
    }

    private void init() {
        Insets insets = new Insets(2, 2, 2, 2);
        add(startLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(startComboBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(endComboBox, new GridBagConstraints(2, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(devicesComboBox, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(changeIdButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(renameButton, new GridBagConstraints(2, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(scrollPane, new GridBagConstraints(0, 2, 3, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(percentLabel, new GridBagConstraints(2, 3, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(checksum, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        add(searchButton, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
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
