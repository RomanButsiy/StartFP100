package base;

public class Base {

    public Base(String[] args) {
        BaseInit.initPlatform();
        BaseInit.getPlatform().init();
    }
}
