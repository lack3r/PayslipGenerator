package io.qbeat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qbeat.DeductionsCalculator;
import io.qbeat.PayslipCalculator;
import io.qbeat.PayslipHistoryDAO;
import io.qbeat.TaxCalculator;
import io.qbeat.exceptions.ConfigurationReadException;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;
import io.qbeat.file.writers.CSVWriter;
import io.qbeat.models.Company;
import io.qbeat.models.Employee;
import io.qbeat.models.PayslipHistory;
import io.qbeat.models.PersonType;
import io.qbeat.utils.FileUtils;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


@Configuration
@ComponentScan(basePackages = {"io.qbeat"})
public class Config {

    private AppConfig appConfig;

    //private GeneralConfig generalConfig;
    //private TaxConfig taxConfig;

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
    public CSVReader getFileReader(){
        return new CSVReader();
    }

    @Bean("csv_writer")
    @DependsOn({"appConfig"})
    public CSVWriter getFileWriter(){
        return new CSVWriter();
    }

    @Bean
    @DependsOn({"appConfig", "csv_reader"})
    public GeneralConfig getGeneralConfig() throws IOException {
        GeneralConfig generalConfig = new GeneralConfig(getFileReader(), appConfig.getGeneralConfigFilename());
        generalConfig.load();
        return generalConfig;
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
    public PayslipHistoryDAO getPayslipHistoryDAO(){
        return new PayslipHistoryDAO(getFileReader(), getFileWriter(), appConfig.getPayslipHistoryFilename());
    }

    @Bean("employeeDeductionsCalculator")
    @DependsOn({"appConfig", "taxConfig", "taxCalculator", "payslipHistoryDAO"})
    public DeductionsCalculator getEmployeeDeductionsCalculator() throws IOException {
        return new DeductionsCalculator(PersonType.EMPLOYEE, getGeneralConfig().getProperties(PersonType.EMPLOYEE), getTaxCalculator(), getPayslipHistoryDAO(), MONTHS_TO_CONSIDER);
    }

    @Bean("employerDeductionsCalculator")
    @DependsOn({"appConfig", "taxConfig", "taxCalculator", "payslipHistoryDAO"})
    public DeductionsCalculator getEmployerDeductionsCalculator() throws IOException {
        return new DeductionsCalculator(PersonType.EMPLOYER, getGeneralConfig().getProperties(PersonType.EMPLOYER), getTaxCalculator(), getPayslipHistoryDAO(), MONTHS_TO_CONSIDER);
    }

//    @Bean("company")
//    @DependsOn({"appConfig"})
//    public ObjectMapper getObjectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
//        return mapper;
//    }

    @Bean("fileUtils")
    public FileUtils getFileUtils() {
        return new FileUtils();
    }

    @Bean
    public Employee getEmployee(){
        return new Employee("111111", "222222", "John Smith", new BigDecimal(1700), LocalDate.of(2019, 4, 1));
    }

    @Bean("company")
    @DependsOn({"appConfig", "fileUtils"})
    public Company getCompany() throws IOException {
        //String companyWithEmployeesFilename = getFileUtils().getFullFilePath(appConfig.getCompanyWithEmployeesFilename());
        //return Company.importFromJSON(companyWithEmployeesFilename, getObjectMapper());
        LinkedList<Employee> employees = new LinkedList<>();
        employees.add(getEmployee());
        return new Company("QBeat Technologies LTD", "Alexandrou Panagouli 52, Larnaca, Cyprus", "+357 99999999", employees);
    }
//
//    @Bean("payslipCalculator")
//    @DependsOn({"payslipHistoryDAO", "employerDeductionsCalculator", "employerDeductionsCalculator"})
//    public PayslipCalculator getPayslipCalculator() throws IOException {
//        return new PayslipCalculator(getCompany(), getEmployeeDeductionsCalculator(), getEmployerDeductionsCalculator());
//    }

    public String getCompanyInfoFilename() {
        return appConfig.getCompanyInfoFilename();
    }

    public String getPayslipHistoryFilename() {
        return appConfig.getPayslipHistoryFilename();
    }

    public String getCompanyWithEmployeesFilename() {
        return appConfig.getCompanyWithEmployeesFilename();
    }

    public String getPayslipsOutputDirectory() {
        return appConfig.getPayslipsOutputDirectory();
    }

    public String getHtmlTemplateFilename() {
        return appConfig.getHtmlTemplateFilename();
    }
}
