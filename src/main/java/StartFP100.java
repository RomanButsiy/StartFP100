import MenuBar.MenuBar;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class StartFP100 {

    private static final String[][] resourceBundle = {{"English", "Українська"}, {"en", "uk"}};
    private static final int selectedLanguage = 1;

    private StartFP100(String title, ResourceBundle bundle) {
        MenuBar menuBar = new MenuBar(bundle);
      //  SerialTerminalPanel panel = new SerialTerminalPanel(menuBar::getPortAndSpeed, bundle);
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.setResizable(true);
      //  frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("localization.resource", new Locale(resourceBundle[1][selectedLanguage]));
        new StartFP100(bundle.getString("titleFrame"), bundle);
    }
}
