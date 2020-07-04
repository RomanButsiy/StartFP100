package base.view.SearchModules;

import SerialDriver.SerialDriver;
import base.Editor;
import base.PreferencesData;
import base.helpers.SendOne;
import base.view.BaseView.BaseView;
import libraries.I7000;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static base.helpers.BaseHelper.parsePortException;

public class SearchModules extends BaseView implements ItemListener {

    private JPanel rootPanel;
    private JProgressBar progressBar1;
    private JComboBox<String> cbStart;
    private JComboBox<String> cbStop;
    private Thread searchThread = new Thread();
    private StringBuffer serialBuffer;
    private SerialDriver serialDriver;
    private List<String[]> list = new ArrayList<>();
    
    public SearchModules(Editor editor) {
        super(editor, "Знайти нові модулі", true, false);
        cbStart.addItemListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                windowClose();
            }
        });
        for(int i= 0; i < 256; i++) {
            cbStart.addItem(String.format("%02X", i));
        }
        serialBuffer = new StringBuffer();
        initButtons("Розпочати пошук", "Гаразд", "Скасувати");
        setViewPanel(rootPanel);
    }

    private void windowClose() {
        if (searchThread.isAlive()) {
            searchThread.stop();
            serialDriver.dispose();
        }
        list.clear();
        setVisible(false);
    }

    private void SearchDevices() {
        serialBuffer.setLength(0);
        String port = PreferencesData.get("serial.port");
        String rate = PreferencesData.get("serial.port.rate", "115200");
        if (port == null) {
            getEditor().statusError("Порт не вибрано");
            startSearch();
            return;
        }
        try {
            serialDriver = new SerialDriver(port, rate, s -> serialBuffer.append(s));
        } catch (Exception e) {
            parsePortException(getEditor(), e);
            startSearch();
            return;
        }
        int wait = PreferencesData.getInteger("response.timeout");
        for (int i = getStart(), j = getStart(), s = getEnd() - getStart() + 1, e = getEnd(); i <= e; i++) {
            try {
                serialDriver.write(I7000.getModuleName(String.format("%02X", i)));
                Thread.sleep(wait);
                progressBar1.setValue(90 * (i - j) / s);
            } catch (Exception ex) {
                getEditor().statusError(ex);
                list.clear();
                progressBar1.setValue(0);
                startSearch();
                serialDriver.dispose();
                return;
            }
        }
        serialDriver.dispose();
        if (serialBuffer.length() == 0) {
            resMessage("Модулів не знайдено");
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(serialBuffer.toString(), "\r");
        String[] knownModules = PreferencesData.getCollection("known.modules.types").toArray(new String[0]);
        while (tokenizer.hasMoreTokens()) {
            String token = I7000.removeCRC(0, 0, tokenizer.nextToken());
            if (token.startsWith("?")) continue;
            String name = token.substring(3);
            String id = token.substring(1, token.length() - name.length());
            SendOne sendOne = new SendOne(getEditor(), I7000.readConfiguration(id));
            String config = I7000.removeCRC(1, sendOne.getResult());
            if (config == null) continue;
            String type = null;
            for (String moduleType : knownModules) {
                if (name.contains(moduleType)) {
                    type = moduleType;
                    break;
                }
            }
            if (type == null) {
                String result = (String) JOptionPane.showInputDialog(this, "Невдалося визначити тип модуля з ID= " + id +
                                "\nВиберіть доступний тип із списку", "Невідомий тип модуля", JOptionPane.QUESTION_MESSAGE, null, knownModules, knownModules[0]);
                if (result == null) continue;
                type = result;
            }
            list.add(new String[]{id, config, type});
        }
       if (list.size() == 0) {
           resMessage("Модулів не знайдено");
           return;
       }
       resMessage("Знайдено " + list.size() + " шт.");
    }

    public int getStart() {
        return cbStart.getSelectedIndex();
    }

    public int getEnd() {
        return cbStop.getSelectedIndex();
    }

    private void startSearch() {
        getButtonLeftPL().setText("Розпочати пошук");
    }

    private void resMessage(String str) {
        progressBar1.setValue(95);
        JOptionPane.showMessageDialog(this, str, "Повідомлкння", JOptionPane.INFORMATION_MESSAGE);
        startSearch();
        progressBar1.setValue(100);
    }

    public List<String[]> getList() {
        return list;
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.DESELECTED) return;
        if (event.getSource() == cbStart) {
            cbStop.removeAllItems();
            for (int i = cbStart.getSelectedIndex(); i < 256; i++) {
                cbStop.addItem(String.format("%02X", i));
            }
            cbStop.setSelectedIndex(cbStop.getItemCount() - 1);
        }
    }

    @Override
    public void leftButtonAction() {
        if (searchThread.isAlive()) {
            searchThread.stop();
            serialDriver.dispose();
            list.clear();
            progressBar1.setValue(0);
            startSearch();
        } else {
            getButtonLeftPL().setText(" Зупинити пошук ");
            searchThread = new Thread(this::SearchDevices);
            searchThread.start();
        }
    }

    public void closeWindow_() {}

    @Override
    public void applyAction() {
        if (searchThread.isAlive()) {
            getEditor().statusNotice("Іде пошук!!!");
            return;
        }
        setVisible(false);
    }

    @Override
    public void closeAction() {
        windowClose();
    }
}
