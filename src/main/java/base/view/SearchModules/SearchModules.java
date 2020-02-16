package base.view.SearchModules;

import base.Editor;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SearchModules extends JDialog implements ItemListener {
    private JPanel rootPanel;
    private JButton cancelButton;
    private JButton okButton;
    private JButton searchButton;
    private JProgressBar progressBar1;
    private JComboBox<String> cbStart;
    private JComboBox<String> cbStop;

    public SearchModules(Editor editor) {
        super(editor);
        setTitle("Знайти нові модулі");
        cbStart.addItemListener(this);
        for(int i= 0; i < 256; i++) {
            cbStart.addItem(String.format("%02X", i));
        }
        add(rootPanel);
        setModal(true);
        setResizable(false);
        pack();
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
}
