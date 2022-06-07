package pkg.fileexplorer;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import pkg.Vars;

public class FileExplorer extends JPanel {
    private enum FileType {
        FILE, FOLDER
    }

    public JPanel top, center;

    private int layer = 0;

    private boolean isMakingProto = false;
    private Component proto = null;
    private FileType protoType = null;

    public FileExplorer(String workdir) {
        this(workdir, 0);
    }

    public FileExplorer(boolean filler) {
        this.layer = 0;
        setBackground(Color.darkGray);
        setLayout(new BorderLayout());
    }

    public FileExplorer(String workdir, int layer) {
        this.layer = layer;

        setBackground(Color.darkGray);
        setLayout(new BorderLayout());

        /* TOP */
        displayFolderHeader(workdir);

        /* CENTER */
        displayFiles(workdir);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!isMakingProto)
                    return;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    removeProto(proto);
                }
            }
        });
    }

    private void displayFolderHeader(String workdir) {
        File dir = new File(workdir);
        top = new JPanel(new FlowLayout(0));
        top.setBorder(BorderFactory.createEmptyBorder(0, this.layer * 5, 0, 0));
        top.setBackground(Color.darkGray);
        add(top, BorderLayout.NORTH);

        JLabel label = new JLabel(dir.getName());
        label.setFont(new Font("Arial", Font.PLAIN, 12));
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
        if (center != null)
            remove(center);
        center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS)); // add left margin
        center.setBorder(BorderFactory.createEmptyBorder(0, (this.layer * 5) + 5, 0, 0));
        center.setBackground(Color.darkGray);
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
            // fileContainer.setBackground(Color.black);
            JButton f = new JButton(file.getName());
            f.setFont(new Font("Arial", Font.PLAIN, 12));
            f.setBackground(Color.darkGray);
            f.setForeground(Color.white);
            f.setContentAreaFilled(false);
            f.setBorder(new EmptyBorder(0, 0, 0, 0));
            f.setMargin(new Insets(0, 0, 0, 0));
            fileContainer.add(f);
            center.add(fileContainer);
            // center.add(f);

            f.addActionListener(openFileHandler(workdir));
        }

        for (File folder : folders)
            center.add(new FileExplorer(workdir + folder.getName() + "/", this.layer + 1));

        if (folders.size() == 0) {
            FileExplorer filler = new FileExplorer(true);
            filler.setBackground(Color.darkGray);
            center.add(filler);
        }

        revalidate();
    }

    private ActionListener newFileFolderHandler(String workdir, FileType type) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newProto(workdir);
                protoType = type;
            }
        };
    }

    private void newProto(String workdir) {
        JPanel fileContainer = new JPanel(new FlowLayout(0));
        fileContainer.setBackground(Color.darkGray);
        // fileContainer.setBackground(Color.black);

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 12));
        nameField.setBackground(Color.darkGray);
        nameField.setForeground(Color.white);
        nameField.setPreferredSize(new Dimension(150, 20));
        nameField.setMargin(new Insets(1, 3, 1, 2));

        fileContainer.add(nameField);
        center.add(fileContainer, 0);

        nameField.requestFocus();

        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                protoEnterHandler(workdir);
            }
        });

        this.proto = nameField;
        protoFocusOutHandler(nameField);
        this.revalidate();
    }

    private void removeProto(Component c) {
        c.setFocusable(false);
        center.remove(c.getParent());
        this.revalidate();
        this.isMakingProto = false;
        this.proto = null;
    }

    private void protoEnterHandler(String workdir) {
        switch (protoType) {
            case FILE:
                fileFromProto(workdir);
                break;
            case FOLDER:
                folderFromProto(workdir);
                break;
        }
    }

    private void fileFromProto(String workdir) {
        String name = ((JTextField) proto).getText();
        File file = new File(workdir + name);
        if (file.exists()) {
            JOptionPane.showMessageDialog(this, "File already exists");
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        removeProto(proto);
        displayFiles(workdir);
    }

    private void folderFromProto(String workdir) {
        String name = ((JTextField) proto).getText();
        File file = new File(workdir + name);
        if (file.exists()) {
            JOptionPane.showMessageDialog(this, "Folder already exists");
            return;
        }
        file.mkdir();
        removeProto(proto);
        displayFiles(workdir);
    }

    private ActionListener openFileHandler(String workdir) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton src = (JButton) e.getSource();

                // read file
                String content = "";
                try (BufferedReader br = new BufferedReader(new FileReader(workdir + src.getText()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        content += line + "\n";
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Vars.editor.open(workdir + src.getText(), content);

            }
        };
    }

    private void protoFocusOutHandler(Component c) {
        isMakingProto = true;
        c.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                removeProto(c);
            }
        });
    }
}
