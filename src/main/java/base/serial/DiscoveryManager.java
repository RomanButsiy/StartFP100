package base.serial;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryManager {

    private final SerialDiscovery serialDiscoverer = new SerialDiscovery();

    public DiscoveryManager() {
        try {
            new Thread(serialDiscoverer).start();
        } catch (Exception e) {
            System.err.println("Error starting discovery method: " + serialDiscoverer.toString());
            e.printStackTrace();
        }
        Thread closeHook = new Thread(() -> {
            try {
                serialDiscoverer.stop();
                System.out.println("Stop");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        closeHook.setName("DiscoveryManager closeHook");
        Runtime.getRuntime().addShutdownHook(closeHook);
    }

    public SerialDiscovery getSerialDiscoverer() {
        return serialDiscoverer;
    }

    public List<String> discovery() {
        return new ArrayList<>(serialDiscoverer.listDiscoveredPorts());
    }


}
