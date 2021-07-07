package io.qbeat;

import io.qbeat.config.AppConfig;
import io.qbeat.config.CSVReader;
import io.qbeat.config.CSVWriter;
import io.qbeat.config.FileReader;
import io.qbeat.config.GeneralConfig;
import io.qbeat.config.TaxConfig;
import io.qbeat.models.CompanyInfo;
import io.qbeat.models.Payslip;

import java.util.List;

public class Main {
    private static final String GENERAL_CONFIG_FILENAME = "general_config.csv";
    private static final String TAX_CONFIG_FILENAME = "tax_config.csv";
    private static final String COMPANY_INFO_FILENAME = "company_info.csv";
    private static final String PAYSLIP_HISTORY_FILENAME = "payslip_history.csv";
    private static final String HTML_TEMPLATE_FILENAME = "payslip_template.html";

    public static void main(String[] args) {
        FileReader fileReader = CSVReader.getInstance();
        GeneralConfig generalConfig = new GeneralConfig(fileReader, GENERAL_CONFIG_FILENAME);
        TaxConfig taxConfig = new TaxConfig(fileReader, TAX_CONFIG_FILENAME);

        AppConfig appConfig = new AppConfig(generalConfig, taxConfig);
        appConfig.load();

        CompanyInfo companyInfo = CompanyInfo.loadFromCSVFile(fileReader, COMPANY_INFO_FILENAME);
        final PayslipHistoryDAO payslipHistoryDAO = new PayslipHistoryDAO(fileReader, new CSVWriter(), PAYSLIP_HISTORY_FILENAME);

        PayslipCalculator payslipCalculator = new PayslipCalculator(companyInfo, appConfig, payslipHistoryDAO);
        List<Payslip> newPayslips = payslipCalculator.calculate();

        HtmlGenerator htmlGenerator = new HtmlGenerator(HTML_TEMPLATE_FILENAME, newPayslips);
        boolean payslipsGeneratedSuccessfully = htmlGenerator.generate();

        // Prevent from saving payslips in payslip history
        // if payslips failed to generate successfully
        if (!payslipsGeneratedSuccessfully) {
            System.out.println("Failed to generate the HTML(s) of payslip(s)");
            System.exit(1);
        }

        System.out.println("HTML(s) of payslip(s) successfully generated");
        payslipHistoryDAO.insertOnDuplicateUpdate(newPayslips);
    }
}
