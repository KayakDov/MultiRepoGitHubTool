package filesOnly;

import java.io.File;
import terminal.Terminal;

/**
 *
 * @author Kayak
 */
public class CppFile extends File {

    private final Terminal terminal;

  
    public CppFile(String file) {
        super(file);
        this.terminal = new Terminal();
    }
    public CppFile(File file){
        this(file.getPath());
    }

    public void compile() {
        compileTo(getAbsolutePath().replace(".cpp", ".exe"));
    }
    
    public void compileTo(String target) {
        terminal.cmd("g++ -o " + target + " " + this);
    }

    public static void main(String[] args) {
        CppFile cppFile = new CppFile("C:/Users/Kayak/Documents/DAST_TA/submissions/cpp/2almarhabialhussain_LATE_27818_5174409_main.cpp");
        cppFile.compile();
    }

}
