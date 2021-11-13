package io.qbeat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qbeat.config.AppConfig;
import io.qbeat.config.Config;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.writers.CSVWriter;
import io.qbeat.file.readers.FileReader;
import io.qbeat.models.Company;
import io.qbeat.models.Payslip;

import java.io.IOException;
import java.util.List;

import io.qbeat.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component("Main")
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            logger.info("Payslip Generator Starting");

            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.register(Config.class);
            ctx.refresh();

            //ctx.close();

            AppConfig appConfig = (AppConfig) ctx.getBean("appConfig");

            logger.info(ctx.getBean(Company.class));

            final PayslipHistoryDAO payslipHistoryDAO = (PayslipHistoryDAO ) ctx.getBean("payslipHistoryDAO");

            List<Payslip> payslipsToBeGenerated = ctx.getBean(PayslipCalculator.class).calculate();

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
