package pkg.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import pkg.Vars;

public class Editor extends JPanel {
    Vars vars;

    String filePath = "";

    String last = "";
    JTextPane textArea;

    boolean ignoreChange = false;
    int lastIgnore = -2;
    int moveCaret = -1;

    boolean isEditable = true;

    HashMap<String, String> bracketPairs = new HashMap<String, String>();
    HashMap<String, Integer> bracketIndices = new HashMap<String, Integer>();

    public Editor() {
        setLayout(new BorderLayout());

        textArea = new JTextPane();
        textArea.setFont(textArea.getFont().deriveFont(16f));

        JScrollPane jsp = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(jsp, BorderLayout.CENTER);

        this.setBackground(Color.gray);
        textArea.setCaretColor(Color.white);
        textArea.setBackground(Color.darkGray);
        textArea.setForeground(Color.white);
        textArea.setSelectionColor(Color.blue);

        // detect save
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (filePath.length() == 0)
                    return;
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    save();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (filePath.length() == 0) {
                    textArea.setEditable(false);
                    textArea.setFocusable(false);
                    textArea.setFocusable(true);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setContents("");
                        }

                    });
                    return;
                } else {
                    textArea.setEditable(true);
                }

                try {
                    String newLetter = e.getDocument().getText(e.getOffset(), e.getLength());

                    // start next line at same spacing as previous line
                    if (newLetter.equals("\n")) {
                        // find last newline
                        int lastNewline = last.lastIndexOf("\n", e.getOffset() - 1);
                        if (lastNewline >= 0) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        int lastSpace = lastNewline + 1;
                                        String o = "";
                                        while (lastSpace < last.length() && lastSpace < e.getOffset()
                                                && last.charAt(lastSpace) == ' ') {
                                            lastSpace++;
                                            o += " ";
                                        }
                                        textArea.getDocument().insertString(e.getOffset() + 1, o, null);
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }

                    }
                    // Ignore closing bracket
                    if (newLetter.length() == 1 && bracketIndices.getOrDefault(newLetter, -1) >= 20
                            && (textArea.getText().length() > e.getOffset() + 1
                                    && bracketIndices.getOrDefault(newLetter, -1) == bracketIndices
                                            .getOrDefault("" + textArea.getText().charAt(e.getOffset() + 1), -2))) {
                        lastIgnore = bracketIndices.getOrDefault(newLetter, -2);
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                ignoreChange = true;
                                textArea.setText(textArea.getText().substring(0, e.getOffset())
                                        + textArea.getText().substring((e.getOffset() + 1)));
                                textArea.setCaretPosition(e.getOffset() + 1);
                            }
                        });
                    }

                    // Insert bracket pair
                    String pair = bracketPairs.getOrDefault(newLetter, "");
                    String firstHalf = textArea.getText().substring(0, e.getOffset() + 1);
                    String lastHalf = textArea.getText().substring(e.getOffset() + e.getLength());
                    if (pair.length() > 0 && lastIgnore != 23 && lastIgnore != 24) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ignoreChange = true;
                                textArea.setText(firstHalf + pair + lastHalf);
                                textArea.setCaretPosition(e.getOffset() + pair.length());
                            }
                        });
                    }

                    lastIgnore = -2;

                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }

                // Replace tab character with 4 spaces
                if (!ignoreChange)
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            last = textArea.getText();
                            replaceTabs(e);
                            ignoreChange = false;
                        }

                    });
                movePointer();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                last = textArea.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                return;
            }

        });

        bracketPairs.put("(", ")");
        bracketPairs.put("{", "}");
        bracketPairs.put("[", "]");
        bracketPairs.put("'", "'");
        bracketPairs.put("\"", "\"");

        bracketIndices.put("(", 10);
        bracketIndices.put("{", 11);
        bracketIndices.put("[", 12);
        bracketIndices.put(")", 20);
        bracketIndices.put("}", 21);
        bracketIndices.put("]", 22);
        bracketIndices.put("\"", 23);
        bracketIndices.put("'", 24);
    }

    private void movePointer() {
        if (moveCaret >= 0) {
            ActionListener task = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (moveCaret >= 0)
                        textArea.setCaretPosition(moveCaret + 3);
                    moveCaret = -1;
                }
            };

            new Timer(1, task).start();
        }
    }

    public void open(String path, String contents) {
        filePath = path;
        textArea.setText(contents);
        textArea.setCaretPosition(0);
        String[] temp = path.split("/");
        Vars.navbar.setEditorTitle(temp[temp.length - 1]);
    }

    public void setContents(String contents) {
        textArea.setText(contents);
    }

    private void save() {
        try {
            FileWriter fw = new FileWriter(filePath);
            fw.write(textArea.getText());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void movePointer(int point) {
        if (point >= 0) {
            ActionListener task = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (point >= 0)
                        textArea.setCaretPosition(point);
                }
            };

            new Timer(1, task).start();
        }
    }

    private void replaceTabs(DocumentEvent e) {
        ignoreChange = true;
        String replaced = textArea.getText().replaceAll("\t", "    ");
        if (replaced.length() - textArea.getText().length() >= 3) {
            moveCaret = textArea.getCaret().getDot();
            textArea.setText(replaced);
        }

    }

    public void setVars(Vars vars) {
        this.vars = vars;
    }

}
