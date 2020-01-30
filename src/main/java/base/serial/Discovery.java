package base.serial;

import java.util.List;

public interface Discovery extends Runnable {

    void start() throws Exception;

    void stop() throws Exception;

    List<String> listDiscoveredPorts();
}
