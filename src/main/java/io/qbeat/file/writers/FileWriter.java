package io.qbeat.file.writers;

import java.io.IOException;
import java.util.List;

public interface FileWriter {
    /**
     * @param filename The name of the file to write on
     * @param line The line to write
     * @param append Flag to determine whether to append the line to file or override the file
     * @throws IOException If an I/O error occurs
     */
    void write(String filename, String line, boolean append) throws IOException;

    /**
     * @param filename The name of the file to write on
     * @param lines The lines to write
     * @param append Flag to determine whether to append the lines to file or override the file
     * @throws IOException If an I/O error occurs
     */
    void write(String filename, List<String> lines, boolean append) throws IOException;
}
