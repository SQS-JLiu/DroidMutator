package mjava.gui.dialog;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class BuildOutputPane extends JScrollPane {
    private JTextPane textPane = new JTextPane();

    private static BuildOutputPane outputPane = null;
    private Color foreground = Color.BLACK; //font color

    public static BuildOutputPane getInstance() {
        if (outputPane == null) {
            synchronized (BuildOutputPane.class) {
                if (outputPane == null) {
                    outputPane = new BuildOutputPane();
                }
            }
        }
        return outputPane;
    }

    private BuildOutputPane() {
        setViewportView(textPane);
        textPane.setEditable(false);
        setPreferredSize(new Dimension(500, 500));
    }

    public void clearTextPane() {
        textPane.setText("");
        textPane.updateUI();
    }

    /**
     * Returns the number of lines in the document.
     */
    private final int getLineCount() {
        return textPane.getDocument().getDefaultRootElement().getElementCount();
    }

    /**
     * Returns the start offset of the specified line.
     *
     * @param line The line
     * @return The start offset of the specified line, or -1 if the line is
     * invalid
     */
    private int getLineStartOffset(int line) {
        Element lineElement = textPane.getDocument().getDefaultRootElement()
                .getElement(line);
        if (lineElement == null)
            return -1;
        else
            return lineElement.getStartOffset();
    }

    /**
     * Clear characters that exceed the line before the number of lines
     */
    private void replaceRange(String str, int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException("end before start");
        }
        Document doc = textPane.getDocument();
        if (doc != null) {
            try {
                if (doc instanceof AbstractDocument) {
                    ((AbstractDocument) doc).replace(start, end - start, str,
                            null);
                } else {
                    doc.remove(start, end - start);
                    doc.insertString(start, str, null);
                }
            } catch (BadLocationException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    public void writePane(String message) {
        try {

            StyledDocument doc = (StyledDocument) textPane
                    .getDocument();

            // Create a style object and then set the style

            // attributes

            Style style = doc.addStyle("StyleName", null);

            // Foreground color

            StyleConstants.setForeground(style, foreground);

            doc.insertString(doc.getLength(), message, style);
        } catch (BadLocationException e) {
             e.printStackTrace();
        }

        // Make sure the last line is always visible

        textPane.setCaretPosition(textPane.getDocument()
                .getLength());

        // Keep the text area down to a certain line count

        int idealLine = 150;
        int maxExcess = 50;

        int excess = getLineCount() - idealLine;
        if (excess >= maxExcess) {
            replaceRange("", 0, getLineStartOffset(excess));
        }
    }
}
