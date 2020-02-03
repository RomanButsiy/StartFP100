package base.view;

import base.Base;
import base.PreferencesData;
import libraries.Theme;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.PrintStream;

import static java.lang.System.err;
import static libraries.Theme.scale;

public class EditorConsole extends JScrollPane {

    private final DefaultStyledDocument document;
    private final JTextPane consoleTextPane;

    private static ConsoleOutputStream out;
    private static ConsoleOutputStream err;

    private SimpleAttributeSet stdOutStyle;
    private SimpleAttributeSet stdErrStyle;

    public EditorConsole(Base base) {
        document = new DefaultStyledDocument();

        consoleTextPane = new JTextPane(document);
        consoleTextPane.setEditable(false);
        DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        consoleTextPane.setFocusTraversalKeysEnabled(false);

        Color backgroundColour = Theme.getColor("console.color");
        consoleTextPane.setBackground(backgroundColour);

        Font consoleFont = Theme.getFont("console.font");
        Font editorFont = PreferencesData.getFont("diagrams.font");
        Font actualFont = new Font(consoleFont.getName(), consoleFont.getStyle(), scale(editorFont.getSize()));

        stdOutStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(stdOutStyle, Theme.getColor("console.output.color"));
        StyleConstants.setBackground(stdOutStyle, backgroundColour);
        StyleConstants.setFontSize(stdOutStyle, actualFont.getSize());
        StyleConstants.setFontFamily(stdOutStyle, actualFont.getFamily());
        StyleConstants.setBold(stdOutStyle, actualFont.isBold());
        StyleConstants.setItalic(stdOutStyle, actualFont.isItalic());

        consoleTextPane.setParagraphAttributes(stdOutStyle, true);

        stdErrStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(stdErrStyle, Theme.getColor("console.error.color"));
        StyleConstants.setBackground(stdErrStyle, backgroundColour);
        StyleConstants.setFontSize(stdErrStyle, actualFont.getSize());
        StyleConstants.setFontFamily(stdErrStyle, actualFont.getFamily());
        StyleConstants.setBold(stdErrStyle, actualFont.isBold());
        StyleConstants.setItalic(stdErrStyle, actualFont.isItalic());

        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(consoleTextPane);

        setViewportView(noWrapPanel);
        getVerticalScrollBar().setUnitIncrement(7);

        FontMetrics metrics = getFontMetrics(actualFont);
        int height = metrics.getAscent() + metrics.getDescent();
        int lines = PreferencesData.getInteger("console.lines");
        setPreferredSize(new Dimension(100, (height * lines)));
        setMinimumSize(new Dimension(100, (height * lines)));

        EditorConsole.init(stdOutStyle, System.out, stdErrStyle, System.err);

        // Add font size adjustment listeners.
        base.addEditorFontResizeListeners(consoleTextPane);
    }

    private static synchronized void init(SimpleAttributeSet outStyle, PrintStream outStream, SimpleAttributeSet errStyle, PrintStream errStream) {
        if (out != null) {
            return;
        }

        out = new ConsoleOutputStream(outStyle, outStream);
        System.setOut(new PrintStream(out, true));

        err = new ConsoleOutputStream(errStyle, errStream);
        System.setErr(new PrintStream(err, true));
    }

    public static void setCurrentEditorConsole(EditorConsole console) {
        out.setCurrentEditorConsole(console);
        err.setCurrentEditorConsole(console);
    }

    public void applyPreferences() {

        Font consoleFont = Theme.getFont("console.font");
        Font editorFont = PreferencesData.getFont("diagrams.font");
        Font actualFont = new Font(consoleFont.getName(), consoleFont.getStyle(), scale(editorFont.getSize()));

        AttributeSet stdOutStyleOld = stdOutStyle.copyAttributes();
        AttributeSet stdErrStyleOld = stdErrStyle.copyAttributes();
        StyleConstants.setFontSize(stdOutStyle, actualFont.getSize());
        StyleConstants.setFontSize(stdErrStyle, actualFont.getSize());

        if (!stdOutStyle.isEqual(stdOutStyleOld) || !stdErrStyle.isEqual(stdOutStyleOld)) {
            out.setAttibutes(stdOutStyle);
            err.setAttibutes(stdErrStyle);

            int start;
            for (int end = document.getLength() - 1; end >= 0; end = start - 1) {
                Element elem = document.getParagraphElement(end);
                start = elem.getStartOffset();
                AttributeSet attrs = elem.getElement(0).getAttributes();
                AttributeSet newAttrs;
                if (attrs.isEqual(stdErrStyleOld)) {
                    newAttrs = stdErrStyle;
                } else if (attrs.isEqual(stdOutStyleOld)) {
                    newAttrs = stdOutStyle;
                } else {
                    continue;
                }
                try {
                    String text = document.getText(start, end - start);
                    document.remove(start, end - start);
                    document.insertString(start, text, newAttrs);
                } catch (BadLocationException e) {
                    if (document.getLength() != 0) {
                        throw new Error(e);
                    }
                }
            }
        }
    }

    public void clear() {
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException ignored) { }
    }

    public void scrollDown() {
        getHorizontalScrollBar().setValue(0);
        getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
    }

    public boolean isEmpty() {
        return document.getLength() == 0;
    }

    public void insertString(String line, SimpleAttributeSet attributes) throws BadLocationException {
        line = line.replace("\r\n", "\n").replace("\r", "\n");
        int offset = document.getLength();
        document.insertString(offset, line, attributes);
    }

    public String getText() {
        return consoleTextPane.getText().trim();
    }

}
