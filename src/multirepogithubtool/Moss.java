package multirepogithubtool;

import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Dov Neimand
 */
public class Moss extends RecursiveTask<String>{

    private final RepoManager rm;

    private final File mossDir;
    private SocketClient socketClient;

    public Moss(RepoManager rm) {
        this.rm = rm;
        rm.getCourseSpecs();
        mossDir = new File(rm.getParentAll(), "mossWork");

        this.socketClient = new SocketClient();
        socketClient.setUserID(rm.getCourseSpecs().mossUserID);
        try {
            socketClient.setLanguage(rm.getCourseSpecs().programmingLanguage);
        } catch (MossException ex) {
            Logger.getLogger(Moss.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a file structure locally that moss likes with the name mossWork
     * This function is called by the moss function so there's no need to call
     * it separately.
     *
     */
    private void setUpMossDir() {

        for (Repo repo : rm) {
//            System.out.println("coppying " + repo.name()
//                    + " answers to mossDir/" + repo.getRepoDir().getName());
            copyToDir(new File(repo.getRepoDir(), rm.getCourseSpecs().answerLocation),
                    new File(mossDir, repo.getRepoDir().getName()));
        }

//        System.out.println("moss directory built");
    }

    /**
     * uploads the base, or does nothing if there is no base
     */
    private void uploadBase() {
        try {
            if (!rm.getBaseRepo().isFile() && !rm.getBaseRepo().isDirectory()) return;
            File base = new File(rm.getBaseRepo(), rm.getCourseSpecs().answerLocation);
            upload(base, true);
        } catch (IOException ex) {
            Logger.getLogger(Moss.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * uploads the base, and if it's a directory, recursively uploads everything
     * therein.
     * @param file 
     */
    private void upload(File file, boolean isBase) throws IOException {
        if (file.isFile()) socketClient.uploadFile(file, isBase);
        if (file.isDirectory()) {
            File[] childeren = file.listFiles();
            for (File child : childeren) upload(child, isBase);
        }
    }


    /**
     * copies a file to a directory.
     *
     * @param file
     * @param toDir
     */
    private static void copyToDir(File file, File toDir) {
        int numAttempts = 10;
        copyToDir(file, toDir, numAttempts);
    }

    /**
     * copies a file to a directory
     *
     * @param from the file to be copied
     * @param toDir the directory moved to
     * @param numAttempts the number of attempts to make before giving up. This
     * is important when attempting to move a file that may not have finished
     * arriving in the location you're trying to get it from. There's a short
     * wait in between each attempt.
     */
    private static void copyToDir(File from, File toDir, int numAttempts) {
        try {
            if (from.isFile()) FileUtils.copyFileToDirectory(from, toDir);
            if (from.isDirectory()) FileUtils.copyDirectory(from, toDir);
        } catch (IOException ex) {
            if (numAttempts > 0)
                try {
                    Thread.sleep(5000);
                    copyToDir(from, toDir, numAttempts - 1);
                    System.err.println("num attempts: " + numAttempts
                            + " copy to dir failed");
                } catch (InterruptedException ex1) {
                    Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex1);
                }
            else {
                Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("File not found: " + from);
            }
        }
    }

    @Override
    protected String compute() {
        setUpMossDir();
        try {
            File[] mossDirectories = mossDir.listFiles();
            socketClient.run();

            uploadBase();

            for (File dir : mossDirectories) upload(dir, false);
            
            socketClient.sendQuery();

            FileUtils.deleteDirectory(mossDir);
            
            return ("Moss results available at " + socketClient.getResultURL().toString());
            
        } catch (Exception ex) {
            Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    
}
