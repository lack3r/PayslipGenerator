package io.qbeat.config;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;
import io.qbeat.models.DeductionProperty;
import io.qbeat.models.PersonType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeductionPercentages {
    private static final Logger logger = LogManager.getLogger(DeductionPercentages.class);

    private static final String EMPTY_STRING = "";

    private final Map<String, DeductionProperty> employeeProperties = new HashMap<>();
    private final Map<String, DeductionProperty> employerProperties = new HashMap<>();
    private final FileReader fileReader;
    private final String filename;
    private boolean isLoaded;

    public DeductionPercentages(FileReader fileReader, String filename) {
        this.fileReader = fileReader;
        this.filename = filename;
    }

    public void load() throws IOException {
        if (isLoaded) {
            return;
        }

        List<String> configLines;
        try {
            configLines = fileReader.read(filename);
        } catch (IOException e) {
            throw new IOException("Could not read Deduction Percentages file", e);
        }

        for (String line : configLines) {
            DeductionProperty property = parseLineAndGetProperty(line);

            addProperty(property);
        }

        isLoaded = true;
        logger.info("{} successfully loaded", getClass().getSimpleName());
    }

    public Map<String, DeductionProperty> getProperties(PersonType personType) {
        return personType == PersonType.EMPLOYEE
                ? Collections.unmodifiableMap(employeeProperties)
                : Collections.unmodifiableMap(employerProperties);
    }

    private DeductionProperty parseLineAndGetProperty(String line) {
        List<String> propertyInfo = CSVReader.splitLine(line);

        return new DeductionProperty(propertyInfo.get(0), propertyInfo.get(1),
                new BigDecimal(propertyInfo.get(2)), parseMaxContributions(propertyInfo.get(3)));
    }

    private BigDecimal parseMaxContributions(String value) {
        return EMPTY_STRING.equals(value)
                ? BigDecimal.ZERO
                : new BigDecimal(value);
    }

    private void addProperty(DeductionProperty property) {
        if (property.getPersonType() == PersonType.EMPLOYEE) {
            addPropertyToMap(employeeProperties, property);
            return;
        }

        if (property.getPersonType() == PersonType.EMPLOYER) {
            addPropertyToMap(employerProperties, property);
        }
    }

    private void addPropertyToMap(Map<String, DeductionProperty> map, DeductionProperty property) {
        if (!map.containsKey(property.getName())) {
            map.put(property.getName(), property);
        }
    }
}
