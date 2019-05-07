package multirepogithubtool;

import java.util.ArrayList;

/**
 *
 * @author Kayak
 */
public class Main {

    public static final String HW = "homework";

    /**
     *
     * @param args
     */
    private static void cloneOrg(String[] args) {
//        RepoManager rm = RepoManager.cloneOrg("CourseSpecs.txt",HW + 5 + "-","EE551_Spring2019_homework5");
        if (args.length == 4) RepoManager.cloneOrg(args[1], args[2], args[3]);
        if (args.length == 5)
            RepoManager.cloneOrg(args[1], args[2], args[3], args[4]);

    }

    /**
     *
     * @param args args[1] should be your course specs file. args[2] is
     * optional, and should be the folder you want to load your files from, if
     * it's not the default folder.
     *
     *
     */
    private static void moss(String[] args) {
        RepoManager rm = null;
        if (args.length == 3) rm = RepoManager.loadFolder(args[1], args[2]);
        if (args.length == 2) rm = RepoManager.loadFolder(args[1]);

        System.out.println(new Moss(rm).compute());
    }

    /**
     * Pass arguments to run from command line. First call clone, then call
     * moss.
     *
     * @param args the first arg should either be moss or clone. The following
     * args should be the arguments for moss or cloneOrg.
     */
    public static void main(String[] args) {

        if (args.length > 0) {
            if (args[0].equals("moss")) moss(args);
            if (args[0].equals("clone")) cloneOrg(args);
        } else {

           
            RepoManager rm = RepoManager.cloneOrg("CourseSpecs.txt", HW + 7
                    + "-", "EE551_Spring2019_homework7");
            
//        RepoManager rm = RepoManager.loadFolder("CourseSpecs.txt");

            Moss moss = new Moss(rm);
            moss.fork();
//        new TestManager(rm).updateAllTestFiles("C:/Users/Kayak/Documents/GitHub/python Class Stuff/EE551_Spring2019_homework7/tests");
            System.out.println(moss.join());
        }

    }

}
