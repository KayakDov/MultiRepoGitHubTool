package specs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SpecsReader extends BufferedReader {

    public SpecsReader(File courseSpecs) throws FileNotFoundException {
        super(new FileReader(courseSpecs));
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        try {
            return line.split(": ")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println(line);
            throw new RuntimeException(ex);
        }
        
    }
}
