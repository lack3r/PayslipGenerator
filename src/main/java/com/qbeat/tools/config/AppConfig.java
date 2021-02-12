package com.qbeat.tools.config;

import com.qbeat.tools.models.GeneralConfigProperty;
import com.qbeat.tools.models.TaxConfigProperty;

import java.util.List;
import java.util.Map;

public class AppConfig {
    private final GeneralConfig generalConfig;
    private final TaxConfig taxConfig;

    public AppConfig(GeneralConfig generalConfig, TaxConfig taxConfig) {
        this.generalConfig = generalConfig;
        this.taxConfig = taxConfig;
    }

    public void load() {
        try {
            generalConfig.load();
            taxConfig.load();
        }
        catch (Exception e) {
            System.out.println("Failed to load application configuration. Invalid format of config files");
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, GeneralConfigProperty> getGeneralProperties(PersonType personType) {
        return generalConfig.getProperties(personType);
    }

    public List<TaxConfigProperty> getTaxProperties() {
        return taxConfig.getProperties();
    }
}
