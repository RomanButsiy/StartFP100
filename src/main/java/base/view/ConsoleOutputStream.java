package base.view;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleOutputStream extends ByteArrayOutputStream {

    private SimpleAttributeSet attributes;
    private final PrintStream printStream;
    private final Timer timer;

    private volatile EditorConsole editorConsole;
    private volatile boolean newLinePrinted;

    public ConsoleOutputStream(SimpleAttributeSet attributes, PrintStream printStream) {
        this.attributes = attributes;
        this.printStream = printStream;
        this.newLinePrinted = false;

        this.timer = new Timer(100, (e) -> {
            if (editorConsole != null && newLinePrinted) {
                editorConsole.scrollDown();
                newLinePrinted = false;
            }
        });
        timer.setRepeats(false);
    }

    public void setAttibutes(SimpleAttributeSet attributes) {
        this.attributes = attributes;
    }

    public void setCurrentEditorConsole(EditorConsole console) {
        this.editorConsole = console;
    }

    public synchronized void flush() {
        String text = toString();

        if (text.length() == 0) {
            return;
        }

        printStream.print(text);
        printInConsole(text);

        reset();
    }

    private void printInConsole(String text) {
        newLinePrinted = newLinePrinted || text.contains("\n");
        if (editorConsole != null) {
            SwingUtilities.invokeLater(() -> {
                try {
                    editorConsole.insertString(text, attributes);
                } catch (BadLocationException ble) {
                    //ignore
                }
            });

            if (!timer.isRunning()) {
                timer.restart();
            }
        }
    }
}