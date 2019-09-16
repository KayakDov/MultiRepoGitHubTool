package terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import multirepogithubtool.Repo;
import multirepogithubtool.RepoManager;

/**
 * This class provides is a poor interface with the terminal. Each instance of
 * this class will will wait for previous commands to complete before issuing
 * new commands.
 *
 * @author Dov
 */
public class Terminal {

    private static final Runtime RUNTIME;
    private Process mostRecentProcess;

    static {
        RUNTIME = Runtime.getRuntime();
    }

    public Terminal() {
        this.mostRecentProcess = null;
    }

    /**
     * Wait for the completion of another process.
     *
     */
    public void waitFor() {
        if (mostRecentProcess != null) try {
            mostRecentProcess.waitFor(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * runs the provided command from the terminal.Note: complicated commands
     * don't work.
     *
     * @param command
     */
    public void cmd(String[] command) {
        try {
            waitFor();
            Arrays.stream(command).forEach(s -> System.out.print(s + " "));
            if(command.length > 1) mostRecentProcess = RUNTIME.exec(command);
            else mostRecentProcess = RUNTIME.exec(command[0]);
            printProcessOutput();
        } catch (IOException ex) {
            Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
    
    public void cmd(String command){
        cmd(new String[]{command});
    }

    /**
     * prints the output of the process to the standard output;
     */
    private void printProcessOutput() {
        new RecursiveAction() {
            @Override
            protected void compute() {
                BufferedReader br = new BufferedReader(new InputStreamReader(mostRecentProcess.getInputStream()));

                try {
                    waitFor();
                    while (br.ready()) System.out.println(br.readLine());
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.fork();
    }
}
