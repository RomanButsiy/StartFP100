import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class PortReader implements SerialPortEventListener {

    private SerialPort serialPort;
    private IUpdateTextArea updateTextArea;

    public PortReader(SerialPort serialPort, IUpdateTextArea updateTextArea) {
        this.updateTextArea = updateTextArea;
        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if(serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                String data = serialPort.readString(serialPortEvent.getEventValue());
                updateTextArea.updateTextArea(data);
            }
            catch (SerialPortException e) {
            }
        }
    }
}
