package base.view.ExperimentSettings;

import base.Editor;
import base.PreferencesData;
import base.helpers.SendOne;
import libraries.I7000;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.stream.IntStream;

import static base.PreferencesData.save;

public class ExperimentSettings extends JDialog implements FocusListener {
    private JPanel rootPanel;
    private JButton cancelButton;
    private JButton okButton;
    private JComboBox<String> signalForm;
    private JTextField signalMin;
    private JTextField signalPeriod;
    private JButton someButton;
    private JLabel minLabel;
    private JLabel maxLabel;
    private JTextField signalMax;
    private final int signalOutRange = PreferencesData.getInteger("signal.out.range");
    private final String responseTimeout = PreferencesData.get("response.timeout", "200");
    private final String signalMaxDef = PreferencesData.get("signal.form.max", getMax());
    private final String signalMinDef = PreferencesData.get("signal.form.min", getMin());

    public ExperimentSettings(Editor editor) {
        super(editor);
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
        ((AbstractDocument) signalMax.getDocument()).setDocumentFilter(new MaxMinDocumentFilter());
        ((AbstractDocument) signalMin.getDocument()).setDocumentFilter(new MaxMinDocumentFilter());
        ((AbstractDocument) signalPeriod.getDocument()).setDocumentFilter(new PeriodDocumentFilter());
        signalMax.addFocusListener(this);
        signalMin.addFocusListener(this);
        signalPeriod.addFocusListener(this);
        String signalPeriodDef = PreferencesData.get("signal.form.period", responseTimeout);
        signalPeriod.setText(signalPeriodDef);
        signalMin.setText(signalMinDef);
        signalMax.setText(signalMaxDef);
        cancelButton.addActionListener(actionEvent -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        okButton.addActionListener(actionEvent -> {
            if (signalMax.getText().equals(signalMin.getText())) {
                signalMax.setText(signalMaxDef);
                signalMin.setText(signalMinDef);
                return;
            }
            if (Double.parseDouble(signalMin.getText()) > Double.parseDouble(signalMax.getText())) {
                String str = signalMin.getText();
                signalMin.setText(signalMax.getText());
                signalMax.setText(str);
                return;
            }
            PreferencesData.set("signal.form.max", signalMax.getText());
            PreferencesData.set("signal.form.min", signalMin.getText());
            PreferencesData.set("signal.form.period", signalPeriod.getText());
            PreferencesData.setInteger("signal.form", signalForm.getSelectedIndex());
            save();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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

    @Override
    public void focusGained(FocusEvent focusEvent) { }

    @Override
    public void focusLost(FocusEvent focusEvent) {
        if (focusEvent.getSource() == signalPeriod) {
            signalPeriod.setText(formatPeriod(signalPeriod.getText()));
        }
        if (focusEvent.getSource() == signalMax) {
            signalMax.setText(formatMaxMin(signalMax.getText()));
        }
        if (focusEvent.getSource() == signalMin) {
            signalMin.setText(formatMaxMin(signalMin.getText()));
        }
    }

    private String formatPeriod(String data) {
        if (data.equals("") || data.equals(".")) data = responseTimeout;
        int result, timeout;
        result = timeout = PreferencesData.getInteger("response.timeout", 200);
        try {
            result = Integer.parseInt(data);
        } catch (NumberFormatException ignored) {}
        if (result < timeout) result = timeout;
        return String.valueOf(result);
    }

    private String formatMaxMin(String data) {
        if (data.equals("") || data.equals(".")) data = "0";
        double result = 0f;
        try {
            result = Double.parseDouble(data);
        } catch (NumberFormatException ignored) {}
        if (result > getMax()) result = getMax();
        if (result < getMin()) result = getMin();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#.######", symbols);
        return decimalFormat.format(result);
    }

    private static class MaxMinDocumentFilter extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
            text = text.replace(',', '.').replaceAll("[^0-9.]+", "");
            if (!text.equals(".") || !fb.getDocument().getText(0, fb.getDocument().getLength()).contains(".")) {
                super.replace(fb, offset, length, text, attributeSet);
            }
        }
    }

    private static class PeriodDocumentFilter extends DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
            text = text.replaceAll("[^0-9]+", "");
            super.replace(fb, offset, length, text, attributeSet);
        }
    }

    private String getIndex() {
        if (signalOutRange == 32) {
            return "В";
        }
        return "мА";
    }

    private int getMax() {
        if (signalOutRange == 32) return 10;
        return 20;
    }

    private int getMin() {
        if (signalOutRange == 31) return 4;
        return 0;
    }

    private String[] getString(){
        int start = 4;
        int end = 20;
        switch (signalOutRange) {
            case 32 : end = 10;
            case 30 : start = 0;
            case 31 : String[] str = new String[end - start + 1];
                for (int i = start, j = 0; i <= end; str[j++] = String.valueOf(i++));
                return str;
        }
        return null;
    }

}
