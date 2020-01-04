package SerialDriver;

import MenuBar.PortAndSpeed;
import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialDriver {

    private static SerialPort serialPort;
    private boolean isInit = false;

    public SerialDriver() {
    }

    public static SerialPort getSerialPort() {
        return serialPort;
    }

    public boolean isInit() {
        return isInit;
    }

    public boolean initPort(PortAndSpeed portAndSpeed) {
        String port = portAndSpeed.getPort();
        if (port == null) return false;
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(Integer.parseInt(portAndSpeed.getSpeed()),
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
        isInit = true;
        return true;
    }

    public void write(String text) {
        try {
            serialPort.writeString(text);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}
