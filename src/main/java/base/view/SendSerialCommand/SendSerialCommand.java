package base.view.SendSerialCommand;

import base.Editor;
import base.helpers.SendOne;
import libraries.CommandHistory;
import libraries.I7000;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SendSerialCommand extends JDialog {

    private final CommandHistory commandHistory = new CommandHistory(100);
    private JTextField command;
    private JTextArea res;
    private JPanel rootPanel;

    public SendSerialCommand(Editor editor) {
        super(editor);
        setTitle("Надіслати команду в послідовний порт");
        res.setRows(16);
        res.setColumns(40);
        res.setEditable(false);
        command.addActionListener( event -> {
            String command = this.command.getText();
            SendOne sendOne = new SendOne(editor, I7000.filter(command));
            commandHistory.addCommand(command);
            this.command.setText("");
            res.append(String.format("Надіслано команду -> %s\nВідповід -> ", command));
            if (sendOne.getResult() == null) {
                res.append("відсутня\n");
                return;
            }
            res.append(I7000.removeCRC(0, sendOne.getResult()) + "\n");
        });
        command.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (commandHistory.hasPreviousCommand()) {
                            command.setText(commandHistory.getPreviousCommand(command.getText()));
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (commandHistory.hasNextCommand()) {
                            command.setText(commandHistory.getNextCommand());
                        }
                        break;
                }
            }
        });
        add(rootPanel);
        setModal(true);
        pack();
    }

}
