package base.processing;

public class Module {

    private String config;
    private String type;
    private int id;
    private String moduleId;
    private boolean isActive = true;
    private boolean isReady = false;

    public Module(int id, String moduleId, String type, String config, boolean isActive) {
        this.id = id;
        this.moduleId = moduleId;
        this.type = type;
        this.config = config;
        this.isActive = isActive;
    }

    public Module(String[] strings, int id) {
        this.id = id;
        this.moduleId = strings[0];
        this.type = strings[2];
        this.config = strings[1];
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getConfig() {
        return config;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String toString() {
        return "Module{" +
                "config='" + config + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", moduleId='" + moduleId + '\'' +
                ", isActive=" + isActive +
                ", isReady=" + isReady +
                '}';
    }

}
