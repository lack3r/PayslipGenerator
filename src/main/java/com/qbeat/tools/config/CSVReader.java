package com.qbeat.tools.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CSVReader implements FileReader {
    protected static final String DELIMITER = ",";
    private static volatile CSVReader instance;

    private CSVReader() {

    }

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

    public static List<String> splitLine(String line) {
        return Arrays.stream(line.split(DELIMITER, -1))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public List<String> read(String filename) {
        java.io.FileReader fileReader;
        try {
            fileReader = getFileReader(filename);
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return readLines(fileReader);
    }

    private java.io.FileReader getFileReader(String filename) throws URISyntaxException, FileNotFoundException {
        URL resource = getClass().getClassLoader().getResource(filename);
        if (Objects.isNull(resource))
            throw new IllegalArgumentException("File not found! " + filename);

        return new java.io.FileReader(new File(resource.toURI()));
    }

    private List<String> readLines(java.io.FileReader fileReader) {
        List<String> fileLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";
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
