package io.qbeat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qbeat.config.AppConfig;
import io.qbeat.config.Config;
import io.qbeat.models.Company;
import io.qbeat.models.Payslip;

import java.io.IOException;
import java.util.List;

import io.qbeat.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component("Main")
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Payslip Generator Starting");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()){

            context.register(Config.class);
            context.refresh();

            AppConfig appConfig = (AppConfig) context.getBean("appConfig");
            FileUtils fileUtils = context.getBean(FileUtils.class);
            String companyWithEmployeesFilename = fileUtils.getFullFilePath(appConfig.getCompanyWithEmployeesFilename());
            Company company = Company.importFromJSON(companyWithEmployeesFilename, context.getBean(ObjectMapper.class));

            logger.info(company);

            final PayslipHistoryDAO payslipHistoryDAO = (PayslipHistoryDAO ) context.getBean("payslipHistoryDAO");

            List<Payslip> payslipsToBeGenerated = context.getBean(PayslipCalculator.class).calculate(company);

            generatePayslips(context.getBean(HtmlGenerator.class), appConfig, payslipsToBeGenerated);

            payslipHistoryDAO.insertOnDuplicateUpdate(payslipsToBeGenerated);
        } catch (Exception e) {
            logger.error("Failed to calculate and generate invoices \n Terminating", e);
        }
        logger.info("Payslip Generator Finished");
    }

    private static void generatePayslips(HtmlGenerator htmlGenerator, AppConfig appConfig, List<Payslip> payslipsToBeGenerated) throws IOException {
        try {
            htmlGenerator.generate(appConfig.getHtmlTemplateFilename(), payslipsToBeGenerated, appConfig.getPayslipsOutputDirectory());
            logger.info("HTML(s) of payslip(s) successfully generated");
        } catch (IOException e) {
            throw new IOException("ERROR: Failed to generate the payslip(s) in HTML format ", e);
        }
    }
}
