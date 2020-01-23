package base.helpers;

public class BasicUserNotifier extends UserNotifier {

    public void showError(String title, String message, Throwable e, int exit_code) {
        if (title == null) title = "Помилка";
        System.err.println(title + ": " + message);
        if (e != null) e.printStackTrace();
        System.exit(exit_code);
    }

    public void showMessage(String title, String message) {
        if (title == null) title = "Повідомлення";
        System.out.println(title + ": " + message);
    }

    public void showWarning(String title, String message, Exception e) {
        if (title == null) title = "Попередження";
        System.out.println(title + ": " + message);
        if (e != null) e.printStackTrace();
    }

}
