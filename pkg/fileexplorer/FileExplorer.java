package pkg.fileexplorer;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pkg.Vars;
import pkg.editor.Editor;

public class FileExplorer extends JPanel {
    Vars vars;

    public FileExplorer(String workdir) {
        setBackground(Color.darkGray);
        setLayout(new BorderLayout());

        File dir = new File(workdir);

        /* TOP */
        JPanel top = new JPanel(new FlowLayout());
        top.setBackground(Color.darkGray);
        add(top, BorderLayout.NORTH);

        JLabel label = new JLabel(dir.getName());
        label.setForeground(Color.white);
        top.add(label);

        JButton createFile = new JButton("New File");
        createFile.setMargin(new Insets(0, 0, 0, 0));
        top.add(createFile);

        JButton createFolder = new JButton("New Folder");
        createFolder.setMargin(new Insets(0, 0, 0, 0));
        top.add(createFolder);

        /* CENTER */
        JPanel center = new JPanel();
        center.setBorder(new EmptyBorder(5, 10, 2, 10));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.darkGray);
        add(center, BorderLayout.CENTER);

        File[] files = new File(workdir).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                center.add(new FileExplorer(workdir + file.getName() + "/"));
                continue;
            }
            JButton f = new JButton(file.getName());
            f.setBackground(Color.darkGray);
            f.setForeground(Color.white);
            f.setContentAreaFilled(false);
            f.setBorder(new EmptyBorder(0, 0, 0, 0));
            f.setMargin(new Insets(0, 0, 0, 0));
            center.add(f);

            f.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton src = (JButton) e.getSource();

                    try {
                        String content = Files.readString(new File(workdir + src.getText()).toPath());
                        System.out.println(content);

                        Vars.editor.setContents(content);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        // file.isDirectory()
    }

    public void setVars(Vars vars) {
        this.vars = vars;
    }
}
