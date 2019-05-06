
package multirepogithubtool;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * Manages the tests in a repo manager
 * @author Kayak
 */
public class TestManager {
    
    private final RepoManager rm;

    /**
     * The constructor
     * @param rm the reopo manager to manage the tests in.
     */
    public TestManager(RepoManager rm) {
        this.rm = rm;
    }
    
    /**
     * replaces all the tests in all the repos with the tests in the
     * provided directory, and pushes the results.
     * @param dir a directory of replacement tests.
     */
    public void updateAllTestFiles(String dir){
        deleteAllTests();
        addAllTests(dir);
        File[] tests = new File(dir).listFiles();
        for(File test: tests) rm.unignore(test.getName());
        rm.pushAllRepos("tests corrected");
    }
    
    
    /**
     * Adds the tests from the directory to each of the student repos
     *
     * @param testDir
     */
    public void addTests(File testDir) {

        try {
            for (Repo targetRepo : rm)
                FileUtils.copyDirectory(testDir, new File(targetRepo.getRepoDir(), "tests"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds all the tests in the given directory
     *
     * @param dir the path and name of the directory containing the tests
     */
    public void addAllTests(String dir) {
        File[] tests = new File(dir).listFiles();
        for (File test : tests) addTest(test);
    }

    public void addTest(File test) {
        try {
            for (Repo targetRepo : rm) {
                System.out.println("Adding test: " + test + " to " + targetRepo);
                FileUtils.copyFileToDirectory(test, new File(targetRepo.getRepoDir(), "tests"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllTests() {
        try {
            for (Repo targetRepo : rm) {
                File tests = new File(targetRepo.getRepoDir(), "tests");
                System.out.println("deleting " + tests.getPath());
                FileUtils.deleteDirectory(tests);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
