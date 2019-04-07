package multirepogithubtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * One file should be saved for each user for each lecture class.
 *  ToDO, remove assignment name from course specs
 * 
 * @author Dov Neimand
 */
public class CourseSpecs {

    class MyBufferedReader extends BufferedReader {

        public MyBufferedReader(File courseSpecs) throws FileNotFoundException {
            super(new FileReader(courseSpecs));
        }

        @Override
        public String readLine() throws IOException {
            return super.readLine().split(": ")[1];
        }
    }

    public final String organizationName, answerLocation, mossUserID, programmingLanguage, gitAccessToken;

    /**
     * This class holds details pertaining to a particular course, github
     * account, and moss account.
     *
     * @param specsFile The file should have the following format: with examples
     * <br>organization name: SIT-ECE
     * <br>assignment name: EE551_Spring2019_homework4
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 85*****1
     * <br>language: python
     * <br>git access token: 680**********************272f44cbf811cdee
     */
    public CourseSpecs(File specsFile) {
        try {
            MyBufferedReader reader = new MyBufferedReader(specsFile);

            this.organizationName = reader.readLine();
            this.answerLocation = reader.readLine();
            this.mossUserID = reader.readLine();
            this.programmingLanguage = reader.readLine();
            this.gitAccessToken = reader.readLine();

            reader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This class holds details pertaining to a particular course, github
     * account, and moss account.
     *
     * @param specsFile The file should have the following format: with examples
     * <br>organization name: SIT-ECE
     * <br>answer location: /src/mypkg/my_answers.py
     * <br>moss id: 85*****1
     * <br>language: python
     * <br>git access token: 680**********************272f44cbf811cdee
     */
    public CourseSpecs(String specsFile) {
        this(new File(specsFile));
    }

}