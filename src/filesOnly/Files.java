package filesOnly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class Files extends ArrayList<File> {

    public Files(File parentDir) {
        this(parentDir.listFiles());
    }

    public Files(File[] files) {
        addAll(Arrays.asList(files));
    }

    public Files(String parentDir) {
        this(new File(parentDir));
    }

    protected Files() {
    }

    private void number() {
        number("");
    }

    public void number(String fileSuffix) {
        for (int i = 0; i < size(); i++)
            try {
                if (get(i).getName().endsWith(fileSuffix))
                    FileUtils.moveFile(get(i), new File(get(i).getParent(), ""
                            + i + get(i).getName()));
            } catch (IOException ex) {
                Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public void runCommand(String commmand) {
        try {
            Runtime.getRuntime().exec(commmand);
        } catch (IOException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * A function that chooses a new name for a file based on its old name
     */
    public static interface NamingConvention {

        public String name(File source);
    }

    public static void main(String[] args) {
        new Files("C:/Users/Kayak/Documents/DAST_TA/submissions/java").forEach(dir ->{
            
            new JavaFile(((File)dir).listFiles()[0]).compile();
        });
    }

}
