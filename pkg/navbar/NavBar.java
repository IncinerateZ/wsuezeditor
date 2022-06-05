package pkg.navbar;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import pkg.Vars;

public class NavBar extends JPanel {
    Vars vars;

    public NavBar() {
        setBackground(Color.lightGray);
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton runBtn = new JButton("Compile & Run");
        add(runBtn);
    }

    public void setVars(Vars vars) {
        this.vars = vars;
    }
}
