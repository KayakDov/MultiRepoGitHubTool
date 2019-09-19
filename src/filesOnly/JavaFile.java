package filesOnly;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import terminal.Terminal;

public class JavaFile extends File {

    private final Terminal terminal;

    public JavaFile(String file) {
        super(file);
        this.terminal = new Terminal();
    }

    public JavaFile(File file) {
        this(file.getPath());
    }

    private String lineWithClassName() {
        try {
            try ( BufferedReader br = new BufferedReader(new FileReader(this))) {
                while (br.ready()) {
                    String line = br.readLine();
                    if (line.contains("class "))
                        if (line.contains("{") || br.readLine().startsWith("{")) {
                            br.close();
                            return line;
                        } else throw new RuntimeException("File " + this
                                    + " requires attention.  Class name is a bit funny.");
                }
            }
            throw new RuntimeException("class name not found");
        } catch (IOException ex) {
            Logger.getLogger(JavaFile.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private String className() {
        String lineWithName = lineWithClassName();
        while (lineWithName.contains("  "))
            lineWithName = lineWithName.replace("  ", " ");
        if (lineWithName.startsWith("public "))
            lineWithName = lineWithName.substring("public ".length());
        if (lineWithName.startsWith("class "))
            lineWithName = lineWithName.substring("class ".length());
        return lineWithName.split(" ")[0];
    }

    public void removePackage() {
        try {
            File tempFile = new File("myTempFile.txt");

            try (BufferedReader reader = new BufferedReader(new FileReader(this)); 
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                
                String currentLine;
                boolean passedPackage = false;
                
                while ((currentLine = reader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (passedPackage || !trimmedLine.contains("package"))
                        writer.write(currentLine
                                + System.getProperty("line.separator"));
                    else passedPackage = true;
                }
            }
            tempFile.renameTo(this);
        } catch (IOException ex) {
            throw new RuntimeException(ex);

        }
    }

    public void fixName() {
        
        removePackage();
        
        String oldName = getName();
        String newName = className() + ".java";

        File folder = new File(getParent(), oldName.replace(".java", ""));

        try {
            FileUtils.moveFile(this, new File(folder, newName));

        } catch (IOException ex) {
            Logger.getLogger(JavaFile.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void compile() {
        terminal.cmd("javac " + getName());
    }

}
