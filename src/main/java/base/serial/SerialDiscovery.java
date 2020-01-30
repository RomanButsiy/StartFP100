package base.serial;

import jssc.SerialPortList;

import java.util.*;

public class SerialDiscovery implements Runnable, Discovery {

    private Timer serialPortsListerTimer;
    private final List<String> serialPorts = new ArrayList<>();
    private final List<String> oldPorts = new ArrayList<>();
    public boolean pausePolling = false;

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
        serialPortsListerTimer = new Timer(SerialDiscovery.class.getName());
        serialPortsListerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!pausePolling) {
                    forceRefresh();
                }
            }
        }, 0, 1000);
    }

    @Override
    public void stop() throws Exception {
        serialPortsListerTimer.cancel();
    }

    @Override
    public List<String> listDiscoveredPorts() {
        return new ArrayList<>(serialPorts);
    }

    public void pausePolling(boolean param) {
        pausePolling = param;
    }

    public synchronized void forceRefresh() {
        List<String> ports = Arrays.asList(SerialPortList.getPortNames());
        if (ports.equals(oldPorts)) {
            return;
        }
        oldPorts.clear();
        oldPorts.addAll(ports);
        setSerialPorts(ports);
    }

    public void setSerialPorts(List<String> newSerialPorts) {
        serialPorts.clear();
        serialPorts.addAll(newSerialPorts);
    }

}
