package filesOnly;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Sorts all of the files in a provided folder into two sub folders, one for
 * java files and the other for cpp files.
 * @author Kayak
 */
public class SortJavaCPP {

    public static void sort(File unsortedFolder) {
        File javaFolder = new File(unsortedFolder, "java"),
                cppFolder = new File(unsortedFolder, "cpp");

        javaFolder.mkdir();
        cppFolder.mkdir();

        Arrays.stream(unsortedFolder.listFiles()).parallel().forEach(file -> {

            try {
                File f = (File) file;
                if (f.isFile())
                    if (f.getName().endsWith("java"))
                        FileUtils.moveFileToDirectory(f, javaFolder, false);
                    else if (f.getName().endsWith("cpp") || f.getName().endsWith("cc"))
                        FileUtils.moveFileToDirectory(f, cppFolder, false);
            } catch (IOException ex) {
                Logger.getLogger(SortJavaCPP.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static void sort(String unsortedFolder) {
        sort(new File(unsortedFolder));
    }

    public static void main(String[] arhs) {
        SortJavaCPP.sort("C:/Users/Kayak/Documents/DAST TA/submissions");
    }

}
