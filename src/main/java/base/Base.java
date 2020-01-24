package base;

public class Base {

    public Base(String[] args) throws Exception {
        BaseInit.initPlatform();
        BaseInit.getPlatform().init();
    }
}
