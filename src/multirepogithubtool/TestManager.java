package multirepogithubtool;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Manages the tests in a repo manager
 *
 * @author Kayak
 */
public class TestManager {

    private final RepoManager rm;

    /**
     * The constructor
     *
     * @param rm the reopo manager to manage the tests in.
     */
    public TestManager(RepoManager rm) {
        this.rm = rm;
    }

    /**
     * replaces all the tests in all the repos with the tests in the provided
     * directory, and pushes the results.
     *
     * @param dir a directory of replacement tests.
     */
    public void updateAllTestFiles(String dir) {
        deleteAllTests();
        addAllTests(dir);
        Arrays.stream(new File(dir).listFiles()).parallel().forEach(test -> rm.unignore(test.getName()));
        rm.pushAllRepos("tests corrected");
    }

    /**
     * Adds the tests from the directory to each of the student repos
     *
     * @param testDir
     */
    public void addTests(File testDir) {
        rm.parallelStream().forEach(
                targetRepo -> {
                    try {
                        FileUtils.copyDirectory(testDir, new File(targetRepo.getRepoDir(), "tests"));
                    } catch (IOException ex) {
                        Logger.getLogger(TestManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }

    /**
     * Adds all the tests in the given directory
     *
     * @param dir the path and name of the directory containing the tests
     */
    public void addAllTests(String dir) {
        Arrays.stream(new File(dir).listFiles()).parallel().
                forEach(test -> addTest(test));
    }

    public void addTest(File test) {

        rm.parallelStream().forEach(targetRepo -> {
            System.out.println("Adding test: " + test + " to " + targetRepo);
            try {
                FileUtils.copyFileToDirectory(test, new File(targetRepo.getRepoDir(), "tests"));
            } catch (IOException ex) {
                Logger.getLogger(TestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public void deleteAllTests() {

        rm.parallelStream().forEach(targetRepo -> {
            try {
                File tests = new File(targetRepo.getRepoDir(), "tests");
                System.out.println("deleting " + tests.getPath());
                FileUtils.deleteDirectory(tests);
            } catch (IOException ex) {
                Logger.getLogger(TestManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
