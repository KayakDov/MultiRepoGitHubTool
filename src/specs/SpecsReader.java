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
        return super.readLine().split(": ")[1];
    }
}
