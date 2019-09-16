package multirepogithubtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import terminal.Terminal;

/**
 *
 * @author Dov Neimand
 */
public class Repo {

    private final File repoDir;
    private Terminal terminal;

    /**
     * Is the folder associated with this repository a repo.
     *
     * @return
     */
    public boolean isRepo() {
        terminal.waitFor();
        return new File(repoDir, ".git").exists();
    }

    /**
     * Designates the attached file as a repo. If it is not, in fact, a repo,
     * then call clone.
     *
     * @param dir a file to be used as a repo.
     */
    public Repo(File dir) {
        this.terminal = new Terminal();
        this.repoDir = dir;
    }

    /**
     * Designates the attached file as a repo. If it is not, in fact, a repo,
     * then call clone.
     *
     * @param dir a file to be used as a repo.
     */
    public Repo(String dir) {
        this(new File(dir));
    }

    /**
     * clones a repo into this object
     *
     * @param file the directory the repo is to be stored in.
     * @param cloneLine
     */
    public Repo(File file, String cloneLine) {
        this(file);
        clone(cloneLine);
    }

    /**
     * deletes the associated directory.
     */
    public void remove() {
        terminal.waitFor();
        try {
            FileUtils.deleteDirectory(repoDir);
        } catch (IOException ex) {
            Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Clones a repo into this directory.Hopefully, the directory is empty.
     *
     * @param gitAddress the clone address
     * @return this repo
     */
    public Repo clone(String gitAddress) {
        terminal.cmd("git clone " + gitAddress + " " + quote(repoDir.getPath()));
        return this;
    }

    /**
     * Appends text to the readme file
     *
     * @param addition the text to be appended.
     */
    public void addToReadMe(String addition) {
        terminal.waitFor();
        try {
            FileUtils.writeStringToFile(new File(repoDir, "README.md"), addition, true);
        } catch (IOException ex) {
            Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * returns a new string in quotes
     *
     * @param inner a string that wants quotation marks
     * @return a new string with quotation marks
     */
    private static String quote(String inner) {
        return "\"" + inner + "\" ";
    }

    /**
     * the git command for this repo
     *
     * @return
     */
    private String git() {
        return "git -C " + quote(repoDir.getPath());
    }

    /**
     * pulls a repo into this directory.
     *
     */
    public void pull() {
        git(PULL);
    }

    public static final String PULL = "pull";

    /**
     * git ADD
     */
    public void add() {
        git(ADD);
    }
    public static final String ADD = "add .";

    /**
     * git commit -m "message"
     *
     * @param message the commit message
     */
    public void commit(String message) {
        git(commitM(message));

    }

    public static final String commitM(String message) {
        return "commit -m" + quote(message);
    }

    /**
     * git push
     *
     */
    public void push() {
        git(PUSH);
    }

    public static final String PUSH = "push";

    /**
     * git ADD *; git commit -m"message"; git push;
     *
     * @param message the commit message.
     */
    public void addCommitPush(String message) {
        add();
        commit(message);
        push();
    }

    /**
     * the directory associated with this repo.
     *
     * @return
     */
    public File getRepoDir() {
        terminal.waitFor();
        return repoDir;
    }

    /**
     * removes a line from the gitignore file
     *
     * @param remove the line to be removed
     */
    public void unignore(String remove) {
        terminal.waitFor();
        try {
            File gitIgnore = new File(repoDir, ".gitignore");
            List<String> lines = FileUtils.readLines(gitIgnore);
            List<String> updatedLines = lines.stream().filter(s -> !s.contains(remove)).collect(Collectors.toList());
            FileUtils.writeLines(gitIgnore, updatedLines, false);
        } catch (IOException ex) {
            Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * issues the given git for this repo.
     *
     * @param command recommended that you pass one of the prebuilt complete.
     */
    public void git(String command) {
        if (command.startsWith("clone"))
            terminal.cmd("git " + command + quote(repoDir.getPath()));
        else terminal.cmd(Repo.this.git() + command);
    }

    /**
     * the repo's full name
     *
     * @return
     */
    public String name() {
        terminal.waitFor();
        String url_1 = "url = git@github.com:",
                url_2 = "url = https://github.com/",
                url = null;
        boolean found = false;

        try {
            File config = new File(repoDir, ".git/config");
            try ( BufferedReader reader = new BufferedReader(new FileReader(config))) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.contains(url_1)) {
                        url = url_1;
                        found = true;
                    }
                    if (line.contains(url_2)) {
                        url = url_2;
                        found = true;
                    }
                    if (found) {
                        reader.close();
                        return line.substring(line.indexOf(url) + url.length(), line.indexOf(".git"));
                    }

                }
            }
            return null;

        } catch (IOException ex) {
            Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    public void TravisLogs() {

//            String command = "C:/cygwin64/bin/bash.exe -c travis logs -r " + name();
        String command = "C:/Ruby25-x64/bin/travis.bat logs -r " + name();

        System.out.println(command);
        terminal.cmd(command.split(" "));

    }

    public static void main(String[] args) {
//        Repo repo = new Repo("tempFileStorage/submissions_homework2/homework2-a-ShirleyLii");

        Repo testRepo = new Repo("C:/Users/Kayak/Documents/GitHub/python Class Stuff/homework-2-KayakDov");

//        testRepo.PULL();
        testRepo.checkout("2019", "02", "27", "00", "00", "00");

//        repo.TravisLogs();
    }

//git checkout 'master@{1979-02-26 18:30:00}'
    public void checkout(String year, String month, String day, String hour, String minute, String second) {
        try {
            String time = year + "-" + month + "-" + day + " " + hour + ":"
                    + minute
                    + ":" + second;
//        git("checkout 'master@{" + time + "}'");

            String command = git()
                    + "checkout `git rev-list -n 1 --first-parent --before=\""
                    + time + "\" master`";
//"checkout 'master@{" + time + "}'";
//            String command = "ping " + "127.0.0.1";
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(new String[]{"C:/Program Files/Git/bin/bash.exe", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            while (reader.ready()) System.out.println(reader.readLine());

            git();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Repo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//            String time = year + "-" + month + "-" + day +" " + hour + ":" + minute + ":" + second;
////        git("checkout 'master@{" + time + "}'");
//        git("checkout `git rev-list -n 1 --first-parent --before= \"" + time + "\" master`");
//    
}
