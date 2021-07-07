package io.qbeat.config;

import io.qbeat.file.readers.FileReader;

public abstract class Config {
    protected final FileReader fileReader;
    protected final String filename;
    protected boolean isLoaded = false;

    protected Config(FileReader fileReader, String filename) {
        this.fileReader = fileReader;
        this.filename = filename;
    }

    public abstract void load();
}
