package base.view.ExperimentSettings;

import base.Editor;
import base.PreferencesData;
import base.helpers.SendOne;
import libraries.I7000;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;

public class ExperimentSettings extends JDialog{
    private final Editor editor;
    private JPanel rootPanel;
    private JButton cancelButton;
    private JButton okButton;
    private JComboBox<String> signalForm;
    private JTextField signalMax;
    private JTextField signalMin;
    private JTextField signalPeriod;
    private JButton someButton;
    private JLabel minLabel;
    private JLabel maxLabel;

    public ExperimentSettings(Editor editor) {
        super(editor);
        this.editor = editor;
        String value = getIndex();
        setTitle("Налаштування експерименту");
        for (String signal : Editor.signals) {
            signalForm.addItem(signal);
        }
        signalForm.setSelectedIndex(PreferencesData.getInteger("signal.form"));
        maxLabel.setText(String.format("Максимальне значення (%s):", value));
        minLabel.setText(String.format("Мінімальне значення (%s):", value));
        add(rootPanel);
        setModal(true);
        setResizable(false);
        pack();
        cancelButton.addActionListener(actionEvent -> setVisible(false));
        okButton.addActionListener(actionEvent -> {

            setVisible(false);
        });
        someButton.addActionListener(actionEvent -> {
            String[] str = getString();
            if (str == null) return;
            int lastResult = PreferencesData.getInteger("runtime.last.result", 0);
            if (lastResult >= str.length) lastResult = 0;
            String result = (String) JOptionPane.showInputDialog(null, "Надіслати одне значення\nдля перевірки модуля",
                    String.format("Виберіть значення (%s)", value), JOptionPane.QUESTION_MESSAGE, null, str, str[lastResult]);
            if (result == null) return;
            PreferencesData.setInteger("runtime.last.result", IntStream.range(0, str.length).filter(i -> str[i].equals(result)).findFirst().orElse(0));
            if (PreferencesData.get("runtime.dac.module") == null) {
                editor.statusError("Id цифро-аналогового перетворювача не вказано");
                return;
            }

            String command = I7000.setAnalogOutTechnicalUnits(PreferencesData.get("runtime.dac.module"), (float) Integer.parseInt(result));
            SendOne sendOne = new SendOne(editor, command);
            String response = I7000.removeCRC(0, sendOne.getResult());
            if (response == null) {
                editor.statusError("Цифро-аналогового перетворювач не відповідає");
                return;
            }
            if (response.contains(">")) {
                editor.statusNotice(String.format("На виході цифро-аналогового перетворювача %s %s", I7000.formatTypeTechnicalUnits(result), value));
                return;
            }
            if (response.contains("?")) {
                editor.statusError("Вихід за межі дозволеного діапазоеу");
                return;
            }
            editor.statusError("Недопучтима команда");
        });
    }

    private String getIndex() {
        if (PreferencesData.getInteger("signal.out.range") == 32) {
            return "В";
        }
        return "мА";
    }

    private String[] getString(){
        int start = 4;
        int end = 20;
        switch (PreferencesData.getInteger("signal.out.range")) {
            case 32 : end = 10;
            case 30 : start = 0;
            case 31 : String[] str = new String[end - start + 1];
                for (int i = start, j = 0; i <= end; str[j++] = String.valueOf(i++));
                return str;
        }
        return null;
    }

}
