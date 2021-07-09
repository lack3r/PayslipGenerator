package io.qbeat.config;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;
import io.qbeat.models.GeneralConfigProperty;
import io.qbeat.models.PersonType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralConfig {
    private static final Logger logger = LogManager.getLogger(GeneralConfig.class);

    private static final String EMPTY_STRING = "";

    private final Map<String, GeneralConfigProperty> employeeProperties = new HashMap<>();
    private final Map<String, GeneralConfigProperty> employerProperties = new HashMap<>();
    private final FileReader fileReader;
    private final String filename;
    private boolean isLoaded;

    public GeneralConfig(FileReader fileReader, String filename) {
        this.fileReader = fileReader;
        this.filename = filename;
    }

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
        logger.info(getClass().getSimpleName() + " successfully loaded");
    }

    public Map<String, GeneralConfigProperty> getProperties(PersonType personType) {
        return personType == PersonType.EMPLOYEE
                ? Collections.unmodifiableMap(employeeProperties)
                : Collections.unmodifiableMap(employerProperties);
    }

    private GeneralConfigProperty parseLineAndGetProperty(String line) {
        List<String> propertyInfo = CSVReader.splitLine(line);

        return new GeneralConfigProperty(propertyInfo.get(0), propertyInfo.get(1),
                new BigDecimal(propertyInfo.get(2)), parseMaxContributions(propertyInfo.get(3)));
    }

    private BigDecimal parseMaxContributions(String value) {
        return EMPTY_STRING.equals(value)
                ? BigDecimal.ZERO
                : new BigDecimal(value);
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
