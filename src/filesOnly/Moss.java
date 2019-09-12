
package filesOnly;

import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import multirepogithubtool.RepoManager;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Kayak
 */
public class Moss extends RecursiveTask<String>{
    
    private File origDir, mossDir;
    private SocketClient socketClient;
    

    public Moss(String folder, String specs) {
        origDir = new File(folder);
        mossDir = new File(origDir, "mossWork");

        this.socketClient = new SocketClient();
        socketClient.setUserID(rm.getCourseSpecs().mossUserID);
        try {
            socketClient.setLanguage(rm.getCourseSpecs().programmingLanguage);
        } catch (MossException ex) {
            Logger.getLogger(multirepogithubtool.Moss.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String getMoss

    /**
     * uploads a file, and if it's a directory, recursively uploads everything
     * therein.
     * @param file 
     */
    private void upload(File file, boolean isBase) throws IOException {
        if (file.isFile()) 
            SocketClient.uploadFile(file, isBase);
    
        if (file.isDirectory()) {
            File[] childeren = file.listFiles();
            for (File child : childeren) upload(child, isBase);
        }
    }

    
    private void setUpMossDir(){
        
    }
    @Override
    protected String compute() {
         setUpMossDir();
        try {
            File[] mossDirectories = mossDir.listFiles();
            SocketClient.run();

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
    
    
    
}
