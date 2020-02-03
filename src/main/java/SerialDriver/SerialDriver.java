package SerialDriver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialDriver implements SerialPortEventListener {

    private static SerialPort serialPort;
    private IDataReadAction dataReadAction;

    public SerialDriver(String port, String rate, IDataReadAction dataReadAction) throws SerialPortException {
        this.dataReadAction = dataReadAction;
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(Integer.parseInt(rate),
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.addEventListener(this);
        } catch (SerialPortException e) {
            dispose();
            throw e;
        }
    }

    public void dispose(){
        if (serialPort != null) {
            try {
                if (serialPort.isOpened()) {
                    serialPort.closePort();  // close the port
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            } finally {
                serialPort = null;
            }
        }
    }

    public void write(String text) throws SerialPortException {
        try {
            serialPort.writeString(text);
        } catch (SerialPortException e) {
            dispose();
            throw e;
        }

    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if(serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                String data = serialPort.readString(serialPortEvent.getEventValue());
                dataReadAction.dataReadAction(data);
            }
            catch (SerialPortException ignored) {
            }
        }
    }
}
