package pkg.fileexplorer;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import pkg.Vars;

public class FileExplorer extends JPanel {
    private enum FileType {
        FILE, FOLDER
    }

    Vars vars;

    JPanel top, center;

    private int layer = 0;

    public FileExplorer(String workdir) {
        this(workdir, 0);
    }

    public FileExplorer(String workdir, int layer) {
        this.layer = layer;

        setBackground(Color.darkGray);
        setLayout(new BorderLayout());

        /* TOP */
        displayFolderHeader(workdir);

        /* CENTER */
        displayFiles(workdir);
    }

    private void displayFolderHeader(String workdir) {
        File dir = new File(workdir);
        top = new JPanel(new FlowLayout(0));
        top.setBorder(BorderFactory.createEmptyBorder(0, this.layer * 5, 0, 0));
        top.setBackground(Color.darkGray);
        add(top, BorderLayout.NORTH);

        JLabel label = new JLabel(dir.getName());
        label.setForeground(Color.white);
        top.add(label);

        JButton createFile = new JButton("New File");
        createFile.setMargin(new Insets(0, 0, 0, 0));
        top.add(createFile);

        createFile.addActionListener(newFileFolderHandler(workdir, FileType.FILE));

        JButton createFolder = new JButton("New Folder");
        createFolder.setMargin(new Insets(0, 0, 0, 0));
        top.add(createFolder);

        createFolder.addActionListener(newFileFolderHandler(workdir, FileType.FOLDER));
    }

    private void displayFiles(String workdir) {
        center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS)); // add left margin
        center.setBorder(BorderFactory.createEmptyBorder(0, (this.layer * 5) + 5, 0, 0));
        center.setBackground(Color.darkGray);
        center.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        center.setAlignmentX(0);
        add(center, BorderLayout.WEST);
        File[] files = new File(workdir).listFiles();
        files = files == null ? new File[0] : files;

        ArrayList<File> folders = new ArrayList<File>();

        for (File file : files) {
            if (file.isHidden())
                continue;
            if (file.isDirectory()) {
                folders.add(file);
                continue;
            }
            JPanel fileContainer = new JPanel(new FlowLayout(0));
            fileContainer.setBackground(Color.darkGray);
            JButton f = new JButton(file.getName());
            f.setBackground(Color.darkGray);
            f.setForeground(Color.white);
            f.setContentAreaFilled(false);
            f.setBorder(new EmptyBorder(0, 0, 0, 0));
            f.setMargin(new Insets(0, 0, 0, 0));
            fileContainer.add(f);
            center.add(fileContainer);

            f.addActionListener(openFileHandler(workdir));
        }

        for (File folder : folders)
            center.add(new FileExplorer(workdir + folder.getName() + "/", this.layer + 1));

    }

    private ActionListener newFileFolderHandler(String workdir, FileType type) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (type) {
                    case FILE:
                        protoFile(workdir);
                        break;
                    case FOLDER:
                        break;
                }

                return;
            }
        };
    }

    private void protoFile(String workdir) {
        System.out.println("protoFile");
        JTextField nameField = new JTextField("new file");
    }

    private ActionListener openFileHandler(String workdir) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton src = (JButton) e.getSource();

                try {
                    String content = Files.readString(new File(workdir + src.getText()).toPath());

                    Vars.editor.open(workdir + src.getText(), content);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    public void setVars(Vars vars) {
        this.vars = vars;
    }
}
