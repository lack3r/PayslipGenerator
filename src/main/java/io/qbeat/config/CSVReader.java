package io.qbeat.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CSVReader implements FileReader {
    private static final String DELIMITER = ",";
    private static volatile CSVReader instance;

    /**
     * Prevent external constructor calls
     */
    private CSVReader() {

    }

    /**
     * @return A CSVReader instance
     */
    public static CSVReader getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (CSVReader.class) {
                if (Objects.isNull(instance)) {
                    instance = new CSVReader();
                }
            }
        }

        return instance;
    }

    /**
     * @param line A csv line comma separated
     * @return The columns of the line in a list
     */
    public static List<String> splitLine(String line) {
        return Arrays.stream(line.split(DELIMITER, -1))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * @param filename The filename to read
     * @return The lines of the file in a list
     */
    public List<String> read(String filename) {
        java.io.FileReader fileReader;
        try {
            fileReader = getFileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return readLines(fileReader);
    }

    /**
     * @param filename The filename to create the file reader
     * @return A FileReader object
     * @throws FileNotFoundException if the named file does not exist, is a directory rather than a regular file,
     * or for some other reason cannot be opened for reading.
     */
    private java.io.FileReader getFileReader(String filename) throws FileNotFoundException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String filepath = rootPath + File.separator + filename;

        File file = new File(filepath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File not found: " + filepath);
        }

        return new java.io.FileReader(file);
    }

    /**
     * @param fileReader The file reader to read all lines
     * @return A list with all file lines
     */
    private List<String> readLines(java.io.FileReader fileReader) {
        List<String> fileLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileLines;
    }
}
