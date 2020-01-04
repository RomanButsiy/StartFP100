package MenuBar;

import jssc.SerialPort;
import jssc.SerialPortList;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.ResourceBundle;

class ToolsMenu {
    private final int defaultSpeed = 2;
    private ResourceBundle bundle;
    private PortAndSpeed portAndSpeed;
    private String [] speeds = { String.valueOf(SerialPort.BAUDRATE_300),
            String.valueOf(SerialPort.BAUDRATE_9600),
            String.valueOf(SerialPort.BAUDRATE_115200) };

    private JMenu speedToolsItem, portToolsItem;

    ToolsMenu(ResourceBundle bundle) {
        this.bundle = bundle;
        portAndSpeed = new PortAndSpeed(null, null);
        new Thread(this::portListener).start();
    }

    private void portListener() {
        String[] portNames = null;
        String[] portNamesNew;
        while (true) {
            try {
                Thread.sleep(5000);
                portNamesNew = SerialPortList.getPortNames();
                if (!Arrays.equals(portNamesNew, portNames)) {
                    if (portNamesNew.length == 0) {
                        portToolsItem.setEnabled(false);
                        portToolsItem.setText(bundle.getString("menuPort"));
                        portAndSpeed.setPort(null);
                    } else {
                        JRadioButton [] port = new JRadioButton[portNamesNew.length];
                        ButtonGroup portsButtonGroup = new ButtonGroup();
                        portToolsItem.removeAll();
                        for (int i = 0; i < portNamesNew.length; i++) {
                            port[i] = new JRadioButton(portNamesNew[i]);
                            portsButtonGroup.add(port[i]);
                            port[i].addItemListener(new portItemListener());
                            portToolsItem.add(port[i]);
                        }
                        port[0].setSelected(true);
                        portToolsItem.setEnabled(true);
                    }
                    portNames = portNamesNew;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    JMenu createToolsMenu() {
        JMenu toolsMenu = new JMenu(bundle.getString("menuTitleTools"));
        ButtonGroup speedsButtonGroup = new ButtonGroup();
        JRadioButton [] speed = new JRadioButton[speeds.length];
        speedToolsItem = new JMenu();
        portToolsItem = new JMenu();
        for (int i = 0; i < speeds.length; i++) {
            speed[i] = new JRadioButton(speeds[i]);
            speedsButtonGroup.add(speed[i]);
            speed[i].addItemListener(new speedItemListener());
            speedToolsItem.add(speed[i]);
        }
        if (defaultSpeed >= 0 && defaultSpeed < speeds.length) {
            speed[defaultSpeed].setSelected(true);
        }
        toolsMenu.add(speedToolsItem);
        toolsMenu.add(portToolsItem);
        return toolsMenu;
    }

    public PortAndSpeed getPortAndSpeed() {
        return portAndSpeed;
    }

    private class speedItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) return;
            Object source = e.getSource();
            if (source instanceof JRadioButton) {
                portAndSpeed.setSpeed(((JRadioButton)source).getText());
                speedToolsItem.setText(bundle.getString("menuSpeed") + portAndSpeed.getSpeed());
            }
        }
    }

    private class portItemListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) return;
            Object source = e.getSource();
            if (source instanceof JRadioButton) {
                portAndSpeed.setPort(((JRadioButton)source).getText());
                portToolsItem.setText(bundle.getString("menuPort") + portAndSpeed.getPort());
            }
        }
    }
}
