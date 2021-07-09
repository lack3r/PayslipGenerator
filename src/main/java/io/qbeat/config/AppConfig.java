package io.qbeat.config;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
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
    private String payslipHistoryFilename = "";
    @Getter
    private String htmlTemplateFilename = "";
    @Getter
    private String payslipsOutputDirectory = "";

    private boolean isLoaded = false;

    public void load() throws IOException {

        if (isLoaded) {
            return;
        }

        String rootPath = "";
        FileInputStream fileInputStream = null;
        try {
            rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String appConfigPath = rootPath + "app.properties";

            Properties appProps = new Properties();
            fileInputStream = new FileInputStream(appConfigPath);

            readProperties(appProps);
        } catch (IOException e) {
            throw new IOException("Could not read Configuration File: " + rootPath, e);
        } finally {
            if (fileInputStream != null){
                fileInputStream.close();
            }
        }

        isLoaded = true;
        logger.info(getClass().getSimpleName() + " successfully loaded");
    }

    private void readProperties(Properties appProps) {
        generalConfigFilename = getNormalisedFilenameFromProperties(appProps, "GENERAL_CONFIG_FILENAME");
        taxConfigFilename = getNormalisedFilenameFromProperties(appProps, "TAX_CONFIG_FILENAME");
        companyInfoFilename = getNormalisedFilenameFromProperties(appProps, "COMPANY_INFO_FILENAME");
        payslipHistoryFilename = getNormalisedFilenameFromProperties(appProps, "PAYSLIP_HISTORY_FILENAME");
        htmlTemplateFilename = getNormalisedFilenameFromProperties(appProps, "HTML_TEMPLATE_FILENAME");
        payslipsOutputDirectory = getNormalisedFilenameFromProperties(appProps, "PAYSLIPS_OUTPUT_DIRECTORY");
    }

    private String getNormalisedFilenameFromProperties(Properties properties, String property) {
        //Remove any " in filepath, if any
        return properties.getProperty(property).trim().replace("\"", "");
    }
}
