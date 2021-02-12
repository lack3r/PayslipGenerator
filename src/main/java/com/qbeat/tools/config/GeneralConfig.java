package com.qbeat.tools.config;

import com.qbeat.tools.models.GeneralConfigProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralConfig extends Config {
    private static final String EMPTY_STRING = "";

    private final Map<String, GeneralConfigProperty> employeeProperties = new HashMap<>();
    private final Map<String, GeneralConfigProperty> employerProperties = new HashMap<>();

    public GeneralConfig(FileReader fileReader, String filename) {
        super(fileReader, filename);
    }

    @Override
    public void load() {
        if (isLoaded) {
            return;
        }

        List<String> configLines = fileReader.read(filename);
        for (String line : configLines) {
            GeneralConfigProperty property = parseLineAndGetProperty(line);

            addProperty(property);
        }

        isLoaded = true;
        System.out.println(getClass().getSimpleName() + " successfully loaded");
    }

    public Map<String, GeneralConfigProperty> getProperties(PersonType personType) {
        return personType == PersonType.EMPLOYEE
                ? Collections.unmodifiableMap(employeeProperties)
                : Collections.unmodifiableMap(employerProperties);
    }

    private GeneralConfigProperty parseLineAndGetProperty(String line) {
        List<String> propertyInfo = CSVReader.splitLine(line);

        return new GeneralConfigProperty(propertyInfo.get(0), propertyInfo.get(1),
                Double.parseDouble(propertyInfo.get(2)), parseMaxContributions(propertyInfo.get(3)));
    }

    private double parseMaxContributions(String value) {
        return EMPTY_STRING.equals(value)
                ? 0.0
                : Double.parseDouble(value);
    }

    private void addProperty(GeneralConfigProperty property) {
        if (property.getPersonType() == PersonType.EMPLOYEE) {
            addPropertyToMap(employeeProperties, property);
            return;
        }

        if (property.getPersonType() == PersonType.EMPLOYER) {
            addPropertyToMap(employerProperties, property);
        }
    }

    private void addPropertyToMap(Map<String, GeneralConfigProperty> map, GeneralConfigProperty property) {
        if (!map.containsKey(property.getName())) {
            map.put(property.getName(), property);
        }
    }
}
