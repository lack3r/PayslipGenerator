package com.qbeat.tools.config;

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
