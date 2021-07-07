package io.qbeat.file.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter implements io.qbeat.file.writers.FileWriter {

    /**
     * @param filename The name of csv file to write on
     * @param line     The line to write
     * @param append   Flag to determine whether to append the line to file or override the file
     * @throws IOException If an I/O error occurs
     */
    public void write(String filename, String line, boolean append) throws IOException {
        FileWriter fileWriter = getFileWriter(filename, append);

        writeToFile(fileWriter, line);
    }

    /**
     * @param filename The name of csv file to write on
     * @param lines    The lines to write
     * @param append   Flag to determine whether to append the lines to file or override the file
     * @throws IOException If an I/O error occurs
     */
    public void write(String filename, List<String> lines, boolean append) throws IOException {

        for (int i = 0; i < lines.size(); i++) {
            // When append is false (i.e override current file)
            // Override the file only upon writing the first line
            // After writing the first line then append the next lines
            if (!append && i > 0) {
                append = true;
            }

            write(filename, lines.get(i), append);
        }
    }

    /**
     * @param filename The name of csv file
     * @param append   Flag to determine whether to append or override the file
     * @return A FileWriter object
     * @throws IOException If an I/O error occurs
     */
    private FileWriter getFileWriter(String filename, boolean append) throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String filepath = rootPath + File.separator + filename;


        File file = new File(filepath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File not found: " + filepath);
        }

        return new FileWriter(file, append);
    }

    /**
     * Write a line in file
     *
     * @param fileWriter A FileWriter object
     * @param line       The line to write in file
     * @throws IOException If an I/O error occurs
     */
    private void writeToFile(FileWriter fileWriter, String line) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(line);
        bufferedWriter.newLine();

        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
