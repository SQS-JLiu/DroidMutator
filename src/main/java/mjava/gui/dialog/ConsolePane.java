package mjava.gui.dialog;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class cannot print strings in real time
 */
public class ConsolePane extends JScrollPane {

    private JTextPane textPane = new JTextPane();

    private static ConsolePane console = null;

    public static  ConsolePane getInstance() {
        if(console == null){
            synchronized(ConsolePane.class){
                if (console == null) {
                    console = new ConsolePane();
                }
            }
        }
        return console;
    }

    private ConsolePane() {

        setViewportView(textPane);

        // Set up System.out

        PrintStream mySystemOut = new MyPrintStream(System.out, Color.BLACK);
        System.setOut(mySystemOut);

        // Set up System.err

        PrintStream mySystemErr = new MyPrintStream(System.err, Color.RED);
        System.setErr(mySystemErr);

        textPane.setEditable(false);
        setPreferredSize(new Dimension(660, 500));
    }

    public void clearTextPane(){
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
     * 清除超过行数时前面多出行的字符
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

    class MyPrintStream extends PrintStream {

        private Color foreground; //输出时所用字体颜色


        /**
         * 构造自己的 PrintStream
         * @param out 可传入 System.out 或 System.err, 实际不起作用
         * @param foreground 显示字体颜色
         */
        MyPrintStream(OutputStream out,Color foreground) {
            super(out,true); //使用自动刷新

            this.foreground = foreground;
        }

        /**
         * 在这里重截,所有的打印方法都要调用最底一层的方法
         */
        public void write(byte[] buf, int off, int len) {
            final String message = new String(buf, off, len);
            /** SWING非界面线程访问组件的方式 */
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
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
                        // e.printStackTrace();

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
            });
        }
    }
}
