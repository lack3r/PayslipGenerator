package io.qbeat;

import io.qbeat.config.AppConfig;
import io.qbeat.config.CSVReader;
import io.qbeat.config.CSVWriter;
import io.qbeat.config.FileReader;
import io.qbeat.config.GeneralConfig;
import io.qbeat.config.TaxConfig;
import io.qbeat.models.CompanyInfo;
import io.qbeat.models.Payslip;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        GeneralConfig generalConfig = null;
        TaxConfig taxConfig = null;
        FileReader fileReader = null;
        try {
            appConfig.load();

            fileReader = CSVReader.getInstance();

            generalConfig = new GeneralConfig(fileReader, appConfig.getGeneralConfigFilename());
            generalConfig.load();

            taxConfig = new TaxConfig(fileReader, appConfig.getTaxConfigFilename());
            taxConfig.load();
        } catch (IOException e) {
            System.out.println("Failed to load configuration files \n Terminating");
            e.printStackTrace();
            System.exit(1);
        }

        CompanyInfo companyInfo = CompanyInfo.loadFromCSVFile(fileReader, appConfig.getCompanyInfoFilename());

        final PayslipHistoryDAO payslipHistoryDAO = new PayslipHistoryDAO(fileReader, new CSVWriter(), appConfig.getPayslipHistoryFilename());

        PayslipCalculator payslipCalculator = new PayslipCalculator(companyInfo, taxConfig, generalConfig, payslipHistoryDAO);
        List<Payslip> payslipsToBeGenerated = payslipCalculator.calculate();

        HtmlGenerator htmlGenerator = new HtmlGenerator(appConfig.getHtmlTemplateFilename(), payslipsToBeGenerated, appConfig.getPayslipsOutputDirectory());
        boolean payslipsGeneratedSuccessfully = htmlGenerator.generate();

        // Prevent from saving payslips in payslip history
        // if payslips failed to generate successfully
        if (!payslipsGeneratedSuccessfully) {
            System.out.println("Failed to generate the payslip(s) in HTML format");
            System.exit(1);
        }

        System.out.println("HTML(s) of payslip(s) successfully generated");

        payslipHistoryDAO.insertOnDuplicateUpdate(payslipsToBeGenerated);
    }
}
