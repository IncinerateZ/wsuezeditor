package pkg;

import java.awt.*;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;

import pkg.fileexplorer.FileExplorer;
import pkg.navbar.NavBar;

public class EzEditor {

    public JFrame main;

    public EzEditor(String workspace) {
        this.main = new JFrame("EzEditor for Java");

        Vars.fileExplorer = new FileExplorer(workspace);
        Vars.navbar = new NavBar(workspace);

        main.setLayout(new BorderLayout());
        main.add(Vars.navbar, BorderLayout.NORTH);
        JScrollPane fileExplorerScroller = new JScrollPane(Vars.fileExplorer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        fileExplorerScroller.getVerticalScrollBar().setUnitIncrement(10);
        main.add(fileExplorerScroller, BorderLayout.WEST);
        main.add(new JScrollPane(Vars.editor,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        main.getContentPane().add(Vars.editor);

        main.setSize(1280, 720);
        main.setVisible(true);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Vars.main = this;
    }

    public static void main(String[] args) throws BadLocationException, IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        UIManager.put("FileChooser.saveButtonText", "Open");

        JFileChooser wd = new JFileChooser("./");
        wd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        wd.setDialogTitle("Open Workspace");
        wd.setAcceptAllFileFilterUsed(false);

        int result = wd.showSaveDialog(null);

        if (result == JFileChooser.CANCEL_OPTION)
            System.exit(0);

        String workdir = wd.getSelectedFile() == null ? wd.getCurrentDirectory().getPath()
                : wd.getSelectedFile().getPath();

        // Vars.navbar.workdir = workdir + '/';
        EzEditor main = new EzEditor(workdir + "/");
    };
}