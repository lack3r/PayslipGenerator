package com.qbeat.tools.config;

import java.util.List;

public interface FileReader {
    /**
     * @param filename The name of file to read
     * @return The lines of the file
     */
    List<String> read(String filename);
}
