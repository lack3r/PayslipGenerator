package io.qbeat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qbeat.DeductionsCalculator;
import io.qbeat.PayslipHistoryDAO;
import io.qbeat.TaxCalculator;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.writers.CSVWriter;
import io.qbeat.models.PersonType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;

@Configuration
@ComponentScan(basePackages = {"io.qbeat"})
public class Config {

    private AppConfig appConfig;

    // TODO Hard-coded months
    private static final int MONTHS_TO_CONSIDER = 13;

    @Bean("appConfig")
    public AppConfig getAppConfig() throws IOException {
        appConfig = new AppConfig();
        appConfig.load();
        return appConfig;
    }

    @Bean("csv_reader")
    @DependsOn({"appConfig"})
    public CSVReader getFileReader() {
        return new CSVReader();
    }

    @Bean("csv_writer")
    @DependsOn({"appConfig"})
    public CSVWriter getFileWriter() {
        return new CSVWriter();
    }

    @Bean
    @DependsOn({"appConfig", "csv_reader"})
    public DeductionPercentages getDeductionPercentages() throws IOException {
        DeductionPercentages deductionPercentages = new DeductionPercentages(getFileReader(), appConfig.getDeductionPercentagesFilename());
        deductionPercentages.load();
        return deductionPercentages;
    }

    @Bean("taxConfig")
    @DependsOn("appConfig")
    public TaxConfig getTaxConfig() throws IOException {
        TaxConfig taxConfig = new TaxConfig(getFileReader(), appConfig.getTaxConfigFilename());
        taxConfig.load();
        return taxConfig;
    }

    @Bean("taxCalculator")
    @DependsOn({"appConfig", "taxConfig"})
    public TaxCalculator getTaxCalculator() throws IOException {
        return new TaxCalculator(getTaxConfig().getProperties(), MONTHS_TO_CONSIDER);
    }

    @Bean("payslipHistoryDAO")
    @DependsOn({"appConfig", "csv_reader", "csv_writer"})
    public PayslipHistoryDAO getPayslipHistoryDAO() {
        return new PayslipHistoryDAO(getFileReader(), getFileWriter(), appConfig.getPayslipHistoryFilename());
    }

    @Bean("employeeDeductionsCalculator")
    @DependsOn({"appConfig", "taxConfig", "taxCalculator", "payslipHistoryDAO"})
    public DeductionsCalculator getEmployeeDeductionsCalculator() throws IOException {
        return new DeductionsCalculator(PersonType.EMPLOYEE, getDeductionPercentages().getProperties(PersonType.EMPLOYEE), getTaxCalculator(), getPayslipHistoryDAO(), MONTHS_TO_CONSIDER);
    }

    @Bean("employerDeductionsCalculator")
    @DependsOn({"appConfig", "taxConfig", "taxCalculator", "payslipHistoryDAO"})
    public DeductionsCalculator getEmployerDeductionsCalculator() throws IOException {
        return new DeductionsCalculator(PersonType.EMPLOYER, getDeductionPercentages().getProperties(PersonType.EMPLOYER), getTaxCalculator(), getPayslipHistoryDAO(), MONTHS_TO_CONSIDER);
    }

    @Bean("objectMapper")
    @DependsOn({"appConfig"})
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper;
    }
}
