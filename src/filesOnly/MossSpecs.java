package filesOnly;

import java.io.File;
import java.io.IOException;
import specs.SpecsReader;

/**
 *
 * @author Kayak
 */
public class MossSpecs {

    public final String mossUserID, programmingLanguage;

    /**
     * This class holds details pertaining to a moss account.
     *
     * @param specsFile The file should have the following format: with examples
     * <br>moss id: 85*****1
     * <br>language: python
     */
    public MossSpecs(File specsFile) {
        try {
            try (SpecsReader reader = new SpecsReader(specsFile)) {
                this.mossUserID = reader.readLine();
                this.programmingLanguage = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This class holds details pertaining to a moss account.
     *
     * @param specsFile The file should have the following format: with examples
     * <br>moss id: 85*****1
     * <br>language: python
     */
    public MossSpecs(String specsFile) {
        this(new File(specsFile));
    }    
}
