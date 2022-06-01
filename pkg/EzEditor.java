package pkg;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

public class EzEditor {
    public EzEditor() {
        JFrame main = new JFrame("EzEditor for Java");
        // Vars vars = new Vars();

        main.setLayout(new BorderLayout());
        main.add(Vars.navbar, BorderLayout.NORTH);
        main.add(Vars.fileExplorer, BorderLayout.WEST);
        main.add(Vars.editor, BorderLayout.CENTER);

        main.getContentPane().add(Vars.editor);

        main.setSize(800, 700);
        main.setVisible(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws BadLocationException, IOException {
        EzEditor main = new EzEditor();

        // TODO show files in workspace, and allow user to open java files and save
        // them, and run them
    };
}