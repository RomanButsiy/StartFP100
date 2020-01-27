package base;

public interface RunnerListener {

    public void statusError(String message);

    public void statusError(Exception exception);

    public void statusNotice(String message);
}
