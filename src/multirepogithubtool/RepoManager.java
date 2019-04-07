package multirepogithubtool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * An array list, and manager, of a bunch of repos. This class makes batch
 * commands, like git pull, for a whole bunch of repos, easy. The repos can
 * either be assembled from a folder of repos in memory, or pull from a github
 * org. Be sure to use factories, and not the constructor, to create.
 *
 * @author Dov Neimand
 */
public class RepoManager extends ArrayList<Repo> {

    final public static String CLONE_HERE = "    \"clone_url\": \"";
    private final CourseSpecs courseSpecs;
    private final File gitOrgInfo;
    /**
     * Note, the following file do not, inherently, need to be static. But it
     * keeps things a bit simpler if they are.
     */
    private File parentAll, allSubmitedHomeworkDir;
    private final File baseRepo;

    /**
     * the directory that has all of the files related to this repo manager in
     * it.
     *
     * @return
     */
    public File getParentAll() {
        return parentAll;
    }

    public final static String DEF_FILE_LOC = "tempFileStorage";
    /**
     * Constructor, stores files to the default file location
     * @param courseSpecs
     * @param baseRepoName 
     */
    public RepoManager(String courseSpecs, String baseRepoName) {
        this(courseSpecs, DEF_FILE_LOC, baseRepoName);
    }

    /**
     * The constructor.
     *
     * @param courseSpecs The the constructor for course specs.
     */
    private RepoManager(String courseSpecs, String fileLoc, String baseRepoName) {
        super(100);
        this.courseSpecs = new CourseSpecs(courseSpecs);

        parentAll = new File(fileLoc);
        allSubmitedHomeworkDir = new File(parentAll, "submissions");

        gitOrgInfo = new File(parentAll, this.courseSpecs.organizationName
                + "_info.txt");
        baseRepo = new File(parentAll, baseRepoName);
    }

    
    /**
     * Creates a repo manager by cloning all the repos whose names have have the
     * given prefix in a GitHub organization.
     *
     * @param courseSpecs a file containing the course Specs for this course
     * <br>organization name: SIT-ECE
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 854481551
     * <br>language: python
     * <br>git access token: 680*************************272f44cbf811cdee
     *
     * @param repoNamesStartsWith the repos that you want to clone, there names
     * all start with ...
     * @param baseRepoName the name of the base repo
     * @return a RepoManager that contains the clones
     *
     */
    public static RepoManager cloneOrg(String courseSpecs, String repoNamesStartsWith, String baseRepoName) {

        return cloneOrg(new RepoManager(courseSpecs, baseRepoName), repoNamesStartsWith);
    }
    /**
     * Creates a repo manager by cloning all the repos whose names have have the
     * given prefix in a GitHub organization.
     *
     * @param courseSpecs a file containing the course Specs for this course
     * <br>organization name: SIT-ECE
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 854481551
     * <br>language: python
     * <br>git access token: 680*************************272f44cbf811cdee
     *
     * @param repoNamesStartsWith the repos that you want to clone, there names
     * all start with ...
     * @return a RepoManager that contains the clones
     *
     */
    public static RepoManager cloneOrg(String courseSpecs, String repoNamesStartsWith) {

        return cloneOrg(new RepoManager(courseSpecs, ""), repoNamesStartsWith);
    }

    /**
     * Creates a repo manager by cloning all the repos whose names have have the
     * given prefix in a GitHub organization.
     *
     * @param courseSpecs a file containing the course Specs for this course
     * <br>organization name: SIT-ECE
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 854481551
     * <br>language: python
     * <br>git access token: 680*************************272f44cbf811cdee
     *
     * @param repoNamesStartsWith the repos that you want to clone, there names
     * all start with ...
     * @param baseRepoName the name of the base repo
     * @param downloadTo the folder that the repos will be downloaded to
     * @return a RepoManager that contains the clones
     *
     */
    public static RepoManager cloneOrg(String courseSpecs, String repoNamesStartsWith, String baseRepoName, String downloadTo) {

        return cloneOrg(new RepoManager(courseSpecs, downloadTo, baseRepoName), repoNamesStartsWith);

    }

    /**
     * clones and organizations repos into the provided repo manager
     * @param rm
     * @param repoNamesStartsWith
     * @return the repo manager, now full of repos.
     */
    private static RepoManager cloneOrg(RepoManager rm, String repoNamesStartsWith) {
        rm.setOrganizationData();
        rm.cloneAll(repoNamesStartsWith, rm.baseRepo.getName());

        return rm;
    }

    /**
     *
     * builds a repo manager of all the repos in a given folder.
     *
     * @param courseSpecs a file containing the course Specs for this course
     * <br>organization name: SIT-ECE
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 854481551
     * <br>language: python
     * <br>git access token: 680*************************272f44cbf811cdee
     *
     * @return 
     */
    public static RepoManager loadFolder(String courseSpecs) {

        RepoManager rm = new RepoManager(courseSpecs, baseFrom(""));
        rm.loadReposFromFolder();
        return rm;
    }

    /**
     *
     * builds a repo manager of all the repos in a given folder.
     *
     * @param courseSpecs a file containing the course Specs for this course
     * <br>organization name: SIT-ECE
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 854481551
     * <br>language: python
     * <br>git access token: 680*************************272f44cbf811cdee
     *
     * @param folder loads the repos from the specific folder
     *
     * @return
     */
    public static RepoManager loadFolder(String courseSpecs, String folder) {
        RepoManager rm = new RepoManager(courseSpecs, folder, baseFrom(folder));
        rm.loadReposFromFolder();
        return rm;
    }
    
    /**
     * finds the base file in the given RepoManager folder by selecting the
     * first repo found.
     * @param dir a folder containing submissions and at least one repo/base file
     * @return the name of the base file
     */
    private static String baseFrom(String dir){
        if(dir.equals("")) dir = DEF_FILE_LOC;
        File[] chileren = new File(dir).listFiles();
        for(File child: chileren) if(new Repo(child).isRepo()) return child.getName();
        return "";
    }

    /**
     * Be sure both files already exist. Appends the content in the from file to
     * the end of the to file.
     *
     * @param from
     * @param to
     */
    private void append(File from, File to) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(from));
            BufferedWriter writer = new BufferedWriter(new FileWriter(to, true));

            while (reader.ready()) {
                writer.write(reader.readLine());
                writer.newLine();
            }
            reader.close();
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * determines if the provided github page is after the last one with info on
     * it.
     *
     * @param page a github organization spec page
     * @return true if the page is empty
     */
    private boolean lastPage(File page) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(page));
            reader.readLine();
            return !reader.readLine().contains("{");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * gets all the data from the organizations git account
     *
     */
    private void setOrganizationData() {

        File tempPageCopy = new File(parentAll, "tempPageCopy.txt");
        int page = 1;

        do
            try {
                String command = "curl -s https://api.github.com/orgs/"
                        + courseSpecs.organizationName
                        + "/repos?access_token=" + courseSpecs.gitAccessToken
                        + "&per_page=100&page=" + page++;

                System.out.println(command);

                Process process = Runtime.getRuntime().exec(command);
                FileUtils.copyInputStreamToFile(process.getInputStream(), tempPageCopy);//gitOrgInfo
                process.waitFor(1, TimeUnit.MINUTES);
                append(tempPageCopy, gitOrgInfo);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        while (!lastPage(tempPageCopy));
        tempPageCopy.delete();
    }

    /**
     * The name of a repo to be cloned
     *
     * @param line a line from the organization data
     * @return the repo name
     */
    private static String getCloneName(String line) {
        return line.substring(CLONE_HERE.length(), line.length() - 2);
    }

    /**
     * Gets a list of all the clone strings for assignment submissions
     *
     * @param repoNamesStartsWithFilter collect all the repos that start with
     * this string
     * @param baseRepoName collect a repo with this name as the base
     * @return
     */
    private void cloneAll(String repoNamesStartsWithFilter, String baseRepoName) {//getOrganizationData()
        try {

            try (BufferedReader repoData = new BufferedReader(new FileReader(gitOrgInfo))) {
                while (repoData.ready()) {
                    String line = repoData.readLine();
                    if (line.startsWith(CLONE_HERE))
                        clone(line, repoNamesStartsWithFilter, baseRepoName);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Build this repo manager on a directory created from repos.
     *
     */
    private void loadReposFromFolder() {
        File[] repos = allSubmitedHomeworkDir.listFiles();
        for (File file : repos) {
            Repo repo = new Repo(file);
            if (repo.isRepo()) add(repo);
        }
    }

    /**
     * clones the repo in the line if the filter lets it through
     *
     * @param line a line containing a clone address
     * @param repoNamesStartsWithFilter the name of the repo needs to start with
     * ...
     * @param baseRepoName the name of the base repo: a repo that by design
     * shares commonality with all the other repos.
     */
    private void clone(String line, String repoNamesStartsWithFilter, String baseRepoName) {
        String cloneLine = getCloneName(line);
        String repoName = repoName(cloneLine);

        if (repoName.startsWith(repoNamesStartsWithFilter)) {
            File homeworkSubmission = new File(allSubmitedHomeworkDir, repoName);
            add(new Repo(homeworkSubmission).clone(cloneLine));
        }
        if (!baseRepoName.isEmpty() && repoName.equals(baseRepoName))
            new Repo(baseRepo).clone(cloneLine);
    }

    /**
     * pulls the clone command out of a line that starts with cloning
     * instructions
     *
     * @param cloneName a line contining a clone address
     * @return the clone address of the line
     */
    private String repoName(String cloneName) {
        return cloneName.substring(cloneName.lastIndexOf("/")
                + 1, cloneName.lastIndexOf("."));
    }

    private static int failedCleanUpIndex = 0;

    /**
     * deletes all files generated
     */
    public void cleanUp() {
        try {
            FileUtils.deleteDirectory(parentAll);
        } catch (IOException ex) {
            try {
                Thread.sleep(500);
                if (failedCleanUpIndex++ < 20) cleanUp();
                else throw new RuntimeException(ex);
            } catch (InterruptedException ex1) {
                Logger.getLogger(RepoManager.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    /**
     * The base repo is a repo that, by design, shares commonality with all the
     * other repos.
     *
     * @return the base repo
     */
    public File getBaseRepo() {
        return baseRepo;
    }

    /**
     * remove an ignore git from .gitignore
     *
     * @param line
     */
    public void unignore(String line) { //"test_hidden.py"
        forEach(targetRepo -> targetRepo.unignore(line));

    }

    /**
     * Run a command for each repo
     * @param command 
     */
    public void commandAll(String command) {
        forEach(repo -> repo.git(command));
    }

    /**
     * ADD, commit, and push all repos
     *
     * @param commitMessage
     */
    public void pushAllRepos(String commitMessage) {
        commandAll(Repo.ADD);

        commandAll(Repo.commitM(commitMessage));

        commandAll(Repo.PUSH);

    }

    public void appendToAllReadme(String string) {
        forEach(repo -> repo.addToReadMe(string));
    }

    /**
     * returns the course specs being used.
     *
     * @return
     */
    public CourseSpecs getCourseSpecs() {
        return courseSpecs;
    }

}
