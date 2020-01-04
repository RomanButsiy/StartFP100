package MenuBar;

public class PortAndSpeed {

    private String port, speed;

    public PortAndSpeed(String port, String speed) {
        this.port = port;
        this.speed = speed;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "portAndSpeed{" +
                "port='" + port + '\'' +
                ", speed='" + speed + '\'' +
                '}';
    }
}
