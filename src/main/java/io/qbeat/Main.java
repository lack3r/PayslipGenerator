package io.qbeat;

import io.qbeat.config.Config;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.writers.CSVWriter;
import io.qbeat.file.readers.FileReader;
import io.qbeat.models.CompanyInfo;
import io.qbeat.models.Payslip;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            Config config = loadConfigurationFiles();

            FileReader csvReader = CSVReader.getInstance();
            CompanyInfo companyInfo = CompanyInfo.loadFromCSVFile(csvReader, config.getCompanyInfoFilename());
            final PayslipHistoryDAO payslipHistoryDAO = new PayslipHistoryDAO(csvReader, new CSVWriter(), config.getPayslipHistoryFilename());

            List<Payslip> payslipsToBeGenerated = calculatePayslipsToBeGenerated(config, companyInfo, payslipHistoryDAO);

            generatePayslips(config, payslipsToBeGenerated);

            payslipHistoryDAO.insertOnDuplicateUpdate(payslipsToBeGenerated);
        } catch (Exception e) {
            System.out.println("Failed to calculate and generate invoices \n Terminating");
            e.printStackTrace();
            System.exit(1);
        }
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
            System.out.println("HTML(s) of payslip(s) successfully generated");
        } catch (IOException e) {
            throw new IOException("ERROR: Failed to generate the payslip(s) in HTML format ", e);
        }
    }

    private static List<Payslip> calculatePayslipsToBeGenerated(Config config, CompanyInfo companyInfo, PayslipHistoryDAO payslipHistoryDAO) {
        PayslipCalculator payslipCalculator = new PayslipCalculator(companyInfo, config.getTaxConfig(), config.getGeneralConfig(), payslipHistoryDAO);
        return payslipCalculator.calculate();
    }
}
