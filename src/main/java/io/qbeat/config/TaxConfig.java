package io.qbeat.config;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;
import io.qbeat.models.TaxConfigProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaxConfig {
    private static final Logger logger = LogManager.getLogger(TaxConfig.class);

    private final List<TaxConfigProperty> taxProperties = new ArrayList<>();
    private final FileReader fileReader;
    private final String filename;
    private boolean isLoaded;

    public TaxConfig(FileReader fileReader, String filename) {
        this.fileReader = fileReader;
        this.filename = filename;
    }

    public void load() {
        if (isLoaded) {
            return;
        }

        List<String> configLines = fileReader.read(filename);
        for (String line : configLines) {
            TaxConfigProperty property = parseLineAndGetProperty(line);

            addProperty(property);
        }

        isLoaded = true;
        logger.info(getClass().getSimpleName() + " successfully loaded");
    }

    public List<TaxConfigProperty> getProperties() {
        return Collections.unmodifiableList(taxProperties);
    }

    private TaxConfigProperty parseLineAndGetProperty(String line) {
        List<String> propertyInfo = CSVReader.splitLine(line);

        return new TaxConfigProperty(Integer.parseInt(propertyInfo.get(0)), Integer.parseInt(propertyInfo.get(1)),
                Double.parseDouble(propertyInfo.get(2)));
    }

    private void addProperty(TaxConfigProperty property) {
        taxProperties.add(property);
    }
}
