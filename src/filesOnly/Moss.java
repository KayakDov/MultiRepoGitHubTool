package filesOnly;

import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import multirepogithubtool.RepoManager;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Kayak
 */
public class Moss {

    private File origDir, mossDir;
    private SocketClient socketClient;
    private MossSpecs mossSpecs;

    public Moss(String folder, String specs, String language) {
        origDir = new File(folder);
        mossDir = new File(origDir, "mossWork");
        mossSpecs = new MossSpecs("MossSpecs.txt");

        this.socketClient = new SocketClient();
        socketClient.setUserID(mossSpecs.mossUserID);
        try {
            socketClient.setLanguage(language);
        } catch (MossException ex) {
            Logger.getLogger(multirepogithubtool.Moss.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(compute());

    }

    /**
     * uploads a file, and if it's a directory, recursively uploads everything
     * therein.
     *
     * @param file
     */
    private void upload(File file, boolean isBase) {
        if (file.isFile())
            try {
                socketClient.uploadFile(file, isBase);
            } catch (IOException ex) {
                Logger.getLogger(Moss.class.getName()).log(Level.SEVERE, null, ex);
            }

        if (file.isDirectory()) {
            File[] childeren = file.listFiles();
            for (File child : childeren) upload(child, isBase);
        }
    }

    private void setUpMossDir() {
        mossDir.mkdir();
        Arrays.stream(origDir.listFiles()).parallel().filter(File::isFile).forEach(file -> {
            File f = (File) file;
            File target = new File(mossDir, f.getName());
            target.mkdir();
            try {
                FileUtils.copyFileToDirectory(file, target);
            } catch (IOException ex) {
                Logger.getLogger(Moss.class.getName()).log(Level.SEVERE, null, ex);
            }
            ;
        });

    }

    public String compute() {
        setUpMossDir();
        try {
            File[] mossDirectories = mossDir.listFiles();
            socketClient.run();

            for (File file : mossDirectories) upload(file, false);
//            Arrays.stream(mossDirectories).parallel().forEach(dir -> upload((File)dir, false));

            socketClient.sendQuery();

            FileUtils.deleteDirectory(mossDir);

            return ("Moss results available at "
                    + socketClient.getResultURL().toString());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        SortJavaCPP.sort("C:/Users/Kayak/Documents/DAST_TA/submissions");
        new Moss("C:/Users/Kayak/Documents/DAST_TA/submissions/cpp", "MossSpecs.txt", "cc");
        new Moss("C:/Users/Kayak/Documents/DAST_TA/submissions/java", "MossSpecs.txt", "java");

    }
}
