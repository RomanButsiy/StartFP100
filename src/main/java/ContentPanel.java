import MenuBar.IGetPortAndSpeed;
import SerialDriver.*;
import jssc.SerialPort;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class ContentPanel extends ContentPanelInit {
    private StringBuffer serialBuffer;
    private SerialDriver serialDriver;
    private Thread searchThread;
    public ContentPanel(IGetPortAndSpeed portAndSpeed, ResourceBundle bundle) {
        super(bundle);
        serialDriver = new SerialDriver();
        serialBuffer = new StringBuffer();
        searchThread = new Thread();
        onEnableWindow(false);
        setEnabledSearchButton(false);
        onInitCommand((ActionEvent event) -> {
            if (!serialDriver.isInit()) {
                serialDriver.initPort(portAndSpeed.getPortAndSpeed());
                SerialPort serialPort = SerialDriver.getSerialPort();
                try {
                    serialPort.addEventListener(new PortReader(serialPort, this::dataReadAction), SerialPort.MASK_RXCHAR);
                    JOptionPane.showMessageDialog(null, "Порт успішно ініціалізовано",
                            serialPort.getPortName(), JOptionPane.INFORMATION_MESSAGE);
                    setEnabledSearchButton(true);
                } catch (SerialPortException ignored) {
                }
            }
        });
        onSearchCommand((ActionEvent event) -> {
            if (searchThread.isAlive()) {
                searchThread.stop();
                setTextPercentLabel("Search stopped!!!");
            } else {
                searchThread = new Thread(this::SearchDevices);
                onEnableWindow(false);
                searchThread.start();
            }
        });
    }

    private void SearchDevices() {
        final int SearchPause = 500;
        final short numberOfDevices = 12;
        serialBuffer.setLength(0);
        StringBuilder str = new StringBuilder("$");
        for (short i = 0; i < numberOfDevices; i++) {
            str.append(String.format("%02X", i));
            str.append("M");
            if (checksumIsSelected()) {
                str.append(getCRC(str.toString().toCharArray()));
            }
            str.append("\r");
            serialDriver.write(str.toString());
            str.setLength(1);
            setTextPercentLabel(100 * i / numberOfDevices + "%");
            try {
                Thread.sleep(SearchPause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setTextPercentLabel("100%");
        updateTextArea();
        searchThread.interrupt();
    }

    private void updateTextArea() {
        clearTextArea();
        if (serialBuffer.length() == 0) return;
        StringTokenizer tokenizer = new StringTokenizer(serialBuffer.toString(), "\r");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.length() < 3) {
                appendStringTextArea("Отримано: " + token + " <-- FAILED!!!\n");
                continue;
            }
            appendStringTextArea("Ідентифікатор модуля: " +
                    token.substring(1, 3) +
                    " Ім'я модуля: " +
                    token.substring(3, token.length() - (checksumIsSelected() ? 2 : 0)) +
                    "\n");
        }
        onEnableWindow(true);
    }

    private String getCRC(char[] str) {
        int crc = 0;
        for (char ch : str) crc += ch;
        String reStr = String.format("%02X", crc);
        return reStr.substring(reStr.length() - 2);
    }

    private void dataReadAction(String s) {
        serialBuffer.append(s);
    }
}
