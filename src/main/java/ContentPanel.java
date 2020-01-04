import MenuBar.IGetPortAndSpeed;
import SerialDriver.SerialDriver;
import jssc.SerialPort;

import java.util.ResourceBundle;

public class ContentPanel extends ContentPanelInit {
    private IGetPortAndSpeed portAndSpeed;
    private SerialDriver serialDriver;
    private SerialPort serialPort;
    public ContentPanel(IGetPortAndSpeed getPortAndSpeed, ResourceBundle bundle) {
    }
}
