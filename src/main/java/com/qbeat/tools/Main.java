package com.qbeat.tools;

import com.qbeat.tools.config.AppConfig;
import com.qbeat.tools.config.CSVReader;
import com.qbeat.tools.config.CSVWriter;
import com.qbeat.tools.config.FileReader;
import com.qbeat.tools.config.GeneralConfig;
import com.qbeat.tools.config.TaxConfig;
import com.qbeat.tools.models.CompanyInfo;
import com.qbeat.tools.models.Payslip;

import java.util.List;

public class Main {
    private static final String GENERAL_CONFIG_FILENAME = "general_config.csv";
    private static final String TAX_CONFIG_FILENAME = "tax_config.csv";
    private static final String COMPANY_INFO_FILENAME = "company_info.csv";
    private static final String PAYSLIP_HISTORY_FILENAME = "payslip_history.csv";

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

        // TODO GENERATE PDFs
        boolean pdfsGeneratedSuccessfully = true;
        if (!pdfsGeneratedSuccessfully) {
            System.out.println("Failed to generate the PDF(s) of payslip(s)");
            System.exit(1);
        }

        System.out.println("PDF(s) of payslip(s) successfully generated");
        payslipHistoryDAO.insertOnDuplicateUpdate(newPayslips);
    }
}
