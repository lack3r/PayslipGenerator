package io.qbeat;

import io.qbeat.config.Config;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.writers.CSVWriter;
import io.qbeat.file.readers.FileReader;
import io.qbeat.models.Company;
import io.qbeat.models.Payslip;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            logger.info("Payslip Generator Starting");

            Config config = loadConfigurationFiles();

            FileReader csvReader = CSVReader.getInstance();
            Company company = Company.loadFromCSVFile(csvReader, config.getCompanyInfoFilename());
            final PayslipHistoryDAO payslipHistoryDAO = new PayslipHistoryDAO(csvReader, new CSVWriter(), config.getPayslipHistoryFilename());

            List<Payslip> payslipsToBeGenerated = calculatePayslipsToBeGenerated(config, company, payslipHistoryDAO);

            generatePayslips(config, payslipsToBeGenerated);

            payslipHistoryDAO.insertOnDuplicateUpdate(payslipsToBeGenerated);
        } catch (Exception e) {
            logger.error("Failed to calculate and generate invoices \n Terminating", e);
        }
        logger.info("Payslip Generator Finished");
    }

    private static Config loadConfigurationFiles() throws Exception {
        Config config = new Config();
        try {
            config.load();
        } catch (IOException e) {
            throw new Exception("Failed to load configuration files ", e);
        }
        return config;
    }

    private static void generatePayslips(Config config, List<Payslip> payslipsToBeGenerated) throws IOException {

        try {
            HtmlGenerator htmlGenerator = new HtmlGenerator(config.getHtmlTemplateFilename(), payslipsToBeGenerated, config.getPayslipsOutputDirectory());
            htmlGenerator.generate();
            logger.info("HTML(s) of payslip(s) successfully generated");
        } catch (IOException e) {
            throw new IOException("ERROR: Failed to generate the payslip(s) in HTML format ", e);
        }
    }

    private static List<Payslip> calculatePayslipsToBeGenerated(Config config, Company company, PayslipHistoryDAO payslipHistoryDAO) {
        PayslipCalculator payslipCalculator = new PayslipCalculator(company, config.getTaxConfig(), config.getGeneralConfig(), payslipHistoryDAO);
        return payslipCalculator.calculate();
    }
}
