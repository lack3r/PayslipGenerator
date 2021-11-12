package io.qbeat.config;

import io.qbeat.exceptions.ConfigurationReadException;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;
import lombok.Getter;

import java.io.IOException;

public class Config {
    @Getter
    private AppConfig appConfig;
    @Getter
    private GeneralConfig generalConfig;
    @Getter
    private TaxConfig taxConfig;

    public static Config loadConfigurationFiles() throws ConfigurationReadException {
        Config config = new Config();
        try {
            config.load();
        } catch (IOException e) {
            throw new ConfigurationReadException("Failed to load configuration files ", e);
        }
        return config;
    }

    private void load() throws IOException {
        appConfig = new AppConfig();
        appConfig.load();

        FileReader fileReader = CSVReader.getInstance();

        generalConfig = new GeneralConfig(fileReader, appConfig.getGeneralConfigFilename());
        generalConfig.load();

        taxConfig = new TaxConfig(fileReader, appConfig.getTaxConfigFilename());
        taxConfig.load();
    }

    public String getCompanyInfoFilename() {
        return appConfig.getCompanyInfoFilename();
    }

    public String getPayslipHistoryFilename() {
        return appConfig.getPayslipHistoryFilename();
    }

    public String getCompanyWithEmployeesFilename() {
        return appConfig.getCompanyWithEmployeesFilename();
    }

    public String getPayslipsOutputDirectory() {
        return appConfig.getPayslipsOutputDirectory();
    }

    public String getHtmlTemplateFilename() {
        return appConfig.getHtmlTemplateFilename();
    }
}
