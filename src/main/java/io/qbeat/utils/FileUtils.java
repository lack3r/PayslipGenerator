package io.qbeat.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileUtils {

    public String getFullFilePath(String relativeFilePath) throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");

        if (resource == null) {
            throw new IOException("Could not get resource " + relativeFilePath);
        }

        String rootPath = resource.getPath();
        return rootPath + File.separator + relativeFilePath;
    }
}
