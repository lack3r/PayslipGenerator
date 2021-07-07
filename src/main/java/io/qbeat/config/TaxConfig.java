package io.qbeat.config;

import io.qbeat.models.TaxConfigProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaxConfig extends Config {
    private final List<TaxConfigProperty> taxProperties = new ArrayList<>();

    public TaxConfig(FileReader fileReader, String filename) {
        super(fileReader, filename);
    }

    @Override
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
        System.out.println(getClass().getSimpleName() + " successfully loaded");
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
