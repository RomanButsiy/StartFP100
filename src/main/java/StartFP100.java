import MenuBar.MenuBar;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

import base.Base;
import base.helpers.OSUtils;

public class StartFP100 {

    // sudo usermod -aG dialout roman

    private static final String[][] resourceBundle = {{"English", "Українська"}, {"en", "uk"}};
    private static final int selectedLanguage = 1;

    private StartFP100(String title, ResourceBundle bundle) {
        MenuBar menuBar = new MenuBar(bundle);
        ContentPanel panel = new ContentPanel(menuBar::getPortAndSpeed, bundle);
        menuBar.setSettingsAction(panel::settingsAction);
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.setResizable(true);
        frame.getContentPane().add(panel);
        panel.setFrame(frame);
        frame.setSize(600, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("localization.resource", new Locale(resourceBundle[1][selectedLanguage]));
        if (!OSUtils.isWindows()) {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        }
        System.setProperty("java.net.useSystemProxies", "true");
        try {
            new Base(args);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(255);
        }
        new StartFP100(bundle.getString("titleFrame"), bundle);
    }
}
