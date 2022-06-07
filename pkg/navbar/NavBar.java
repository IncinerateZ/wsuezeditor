package pkg.navbar;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JButton;
import javax.swing.JPanel;

import pkg.Vars;

public class NavBar extends JPanel {
    Vars vars;

    public String workdir;

    private JLabel title = new JLabel("No File Opened");

    public NavBar(String workdir) {
        this.workdir = workdir;
        setBackground(Color.lightGray);
        setLayout(new BorderLayout());

        JButton runBtn = new JButton("Compile & Run");

        runBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UIManager.put("FileChooser.saveButtonText", "Select");
                JFileChooser wd = new JFileChooser(workdir);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Java File", "java");

                wd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                wd.setDialogTitle("Select Main Class");
                wd.setAcceptAllFileFilterUsed(false);
                wd.setFileFilter(filter);

                int result = wd.showSaveDialog(null);

                if (result == JFileChooser.CANCEL_OPTION)
                    return;

                new Thread(new Runnable() {
                    public void run() {
                        File cmds = new File("./rnc.bat");
                        if (cmds.exists())
                            cmds.delete();
                        try {
                            cmds.createNewFile();
                            FileWriter fw = new FileWriter("./rnc.bat");

                            fw.write("cd " + wd.getSelectedFile().getParentFile().getParentFile() + "\n javac " +
                                    wd.getSelectedFile().getParentFile().getName() + "/"
                                    + wd.getSelectedFile().getName() + " \njava "
                                    + wd.getSelectedFile().getParentFile().getName() + "/"
                                    + wd.getSelectedFile().getName().split("\\.")[0]);
                            fw.close();

                            Runtime.getRuntime().exec("cmd.exe /c start rnc.bat");
                            Thread.sleep(1000);
                            Runtime.getRuntime().exec("cmd.exe /c del rnc.bat");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleContainer.setBackground(Color.lightGray);

        title.setFont(new Font("Arial", Font.PLAIN, 14));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        titleContainer.add(title);
        add(titleContainer, BorderLayout.CENTER);
        add(runBtn, BorderLayout.EAST);
    }

    public void setEditorTitle(String name) {
        title.setText(name);
    }

    public void setVars(Vars vars) {
        this.vars = vars;
    }
}
