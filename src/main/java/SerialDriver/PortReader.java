package SerialDriver;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class PortReader implements SerialPortEventListener {

    private SerialPort serialPort;
    private IDataReadAction dataReadAction;

    public PortReader(SerialPort serialPort, IDataReadAction dataReadAction) {
        this.dataReadAction = dataReadAction;
        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if(serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                String data = serialPort.readString(serialPortEvent.getEventValue());
                dataReadAction.dataReadAction(data);
            }
            catch (SerialPortException e) {
            }
        }
    }
}
