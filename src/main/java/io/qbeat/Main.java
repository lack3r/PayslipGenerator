package io.qbeat;

import io.qbeat.config.AppConfig;
import io.qbeat.config.Config;
import io.qbeat.models.Company;
import io.qbeat.models.Payslip;

import java.io.IOException;
import java.util.List;

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

            logger.info(context.getBean(Company.class));

            final PayslipHistoryDAO payslipHistoryDAO = (PayslipHistoryDAO ) context.getBean("payslipHistoryDAO");

            List<Payslip> payslipsToBeGenerated = context.getBean(PayslipCalculator.class).calculate(context.getBean(Company.class));

            try {
                HtmlGenerator htmlGenerator = new HtmlGenerator(appConfig.getHtmlTemplateFilename(), payslipsToBeGenerated, appConfig.getPayslipsOutputDirectory());
                htmlGenerator.generate();
                logger.info("HTML(s) of payslip(s) successfully generated");
            } catch (IOException e) {
                throw new IOException("ERROR: Failed to generate the payslip(s) in HTML format ", e);
            }

            payslipHistoryDAO.insertOnDuplicateUpdate(payslipsToBeGenerated);
        } catch (Exception e) {
            logger.error("Failed to calculate and generate invoices \n Terminating", e);
        }
        logger.info("Payslip Generator Finished");
    }
}
