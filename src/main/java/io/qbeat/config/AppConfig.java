package io.qbeat.config;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class AppConfig {

    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    @Getter
    private String generalConfigFilename = "";
    @Getter
    private String taxConfigFilename = "";
    @Getter
    private String companyInfoFilename = "";
    @Getter
    private String companyWithEmployeesFilename = "";
    @Getter
    private String payslipHistoryFilename = "";
    @Getter
    private String htmlTemplateFilename = "";
    @Getter
    private String payslipsOutputDirectory = "";

    private boolean isLoaded = false;

    void load() throws IOException {

        if (isLoaded) {
            return;
        }

        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        if (resource == null) {
            throw new IOException("Resource could not be found while reading properties file ");
        }

        String rootPath = resource.getPath();
        String appConfigPath = rootPath + "app.properties";

        try (FileInputStream fileInputStream = new FileInputStream(appConfigPath)) {
            Properties appProps = new Properties();
            appProps.load(fileInputStream);
            readProperties(appProps);
        } catch (IOException e) {
            throw new IOException("Could not read Configuration File: " + rootPath, e);
        }

        isLoaded = true;
        logger.info("{} successfully loaded", getClass().getSimpleName());
    }

    private void readProperties(Properties appProps) {
        generalConfigFilename = getNormalisedFilenameFromProperties(appProps, "GENERAL_CONFIG_FILENAME");
        taxConfigFilename = getNormalisedFilenameFromProperties(appProps, "TAX_CONFIG_FILENAME");
        companyInfoFilename = getNormalisedFilenameFromProperties(appProps, "COMPANY_INFO_FILENAME");
        payslipHistoryFilename = getNormalisedFilenameFromProperties(appProps, "PAYSLIP_HISTORY_FILENAME");
        htmlTemplateFilename = getNormalisedFilenameFromProperties(appProps, "HTML_TEMPLATE_FILENAME");
        payslipsOutputDirectory = getNormalisedFilenameFromProperties(appProps, "PAYSLIPS_OUTPUT_DIRECTORY");
        companyWithEmployeesFilename = getNormalisedFilenameFromProperties(appProps,"COMPANY_WITH_EMPLOYEES_FILENAME");
    }

    private String getNormalisedFilenameFromProperties(Properties properties, String property) {
        //Remove any " in filepath, if any
        return properties.getProperty(property).trim().replace("\"", "");
    }
}
