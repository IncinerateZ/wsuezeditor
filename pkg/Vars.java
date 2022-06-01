package pkg;

import pkg.editor.Editor;
import pkg.fileexplorer.FileExplorer;
import pkg.navbar.NavBar;

public class Vars {
    public static FileExplorer fileExplorer = new FileExplorer("./workspace/");
    public static Editor editor = new Editor();
    public static NavBar navbar = new NavBar();

    Vars() {
    }

    void setVars() {
        fileExplorer.setVars(this);
        editor.setVars(this);
        navbar.setVars(this);
    }
}
