package io.qbeat.file.readers;

import java.io.IOException;
import java.util.List;

public interface FileReader {
    /**
     * @param filename The name of file to read
     * @return The lines of the file
     */
    List<String> read(String filename) throws IOException;
}
