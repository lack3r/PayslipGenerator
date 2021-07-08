package io.qbeat;

import io.qbeat.models.Company;
import io.qbeat.models.DeductionsInfo;
import io.qbeat.models.Employee;
import io.qbeat.models.Payslip;
import io.qbeat.utils.DateUtil;
import io.qbeat.utils.DecimalUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

public class HtmlGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlGenerator.class);

    private static final String NA = "N/A";

    private final String templateFilename;
    private final List<Payslip> payslips;
    private final String outputDirectory;

    private final DecimalFormat formatter = new DecimalFormat();

    public HtmlGenerator(String templateFilename, List<Payslip> payslips, String outputDirectory) {
        this.templateFilename = templateFilename;
        this.payslips = payslips;
        this.outputDirectory = outputDirectory;
        configureAmountsFormatter();
    }

    private void configureAmountsFormatter() {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setGroupingUsed(false);
        Currency currency = Currency.getInstance("EUR");
        formatter.setCurrency(currency);
    }

    /**
     * Generates all Invoices
     */
    public void generate() throws IOException {
        for (Payslip payslip : payslips) {
            generatePayslip(payslip);
        }
    }

    /**
     * @param payslip A Payslip object
     */
    private void generatePayslip(Payslip payslip) throws IOException {
        String template = addInfo(loadTemplateFile(), payslip);
        String payslipName = getPayslipFilename(payslip.getEmployee().getId());
        createNewPayslip(template, payslipName);
    }

    /**
     * @param template An html template
     * @param payslip  A Payslip object
     * @return An html with all the values of the payslip
     */
    private String addInfo(String template, Payslip payslip) {
        template = addCompanyInfo(template, payslip.getCompanyInfo());
        template = addEmployeeInfo(template, payslip);
        template = addEmployeeDeductions(template, payslip.getEmployeeDeductionsInfo());
        template = addEmployerDeductions(template, payslip);

        return template;
    }

    /**
     * @param template    An html template
     * @param company A Company object
     * @return An html with all the company info
     */
    private String addCompanyInfo(String template, Company company) {
        return template.replace("{companyName}", company.getName())
                .replace("{companyAddress}", company.getAddress())
                .replace("{companyPhone}", company.getPhone());
    }

    /**
     * @param template An html template
     * @param payslip  A Payslip object
     * @return An html with all the employee info
     */
    private String addEmployeeInfo(String template, Payslip payslip) {
        Employee employee = payslip.getEmployee();
        BigDecimal netSalary = employee.getGrossSalary()
                .subtract(payslip.getEmployeeDeductionsInfo().getTotalDeductionsForMonth());

        return template.replace("{employeeName}", employee.getFullName())
                .replace("{employeeId}", employee.getId())
                .replace("{employeeSocialInsuranceId}", employee.getSocialInsuranceId())
                .replace("{date}", DateUtil.localDateToDateStr(DateUtil.now(), "dd/MM/yyyy"))
                .replace("{month}", DateUtil.localDateToDateStr(DateUtil.now(), "MMMM yyyy"))
                .replace("{employeeGrossSalary}", formatter.format(employee.getGrossSalary()))
                .replace("{employeeTotalEarnings}", formatter.format(employee.getGrossSalary()))
                .replace("{employeeGrossSalaryYearToDate}", NA)
                .replace("{employeeTotalEarningsYearToDate}", NA)
                .replace("{employeeNetSalary}", formatter.format(netSalary));
    }

    /**
     * @param template   An html template
     * @param deductions A DeductionsInfo object of employee
     * @return An html with all the employee deductions
     */
    private String addEmployeeDeductions(String template, DeductionsInfo deductions) {
        return template.replace("{employeeSocialInsuranceForMonth}", formatter.format(deductions.getSocialInsuranceForMonth()))
                .replace("{employeeSocialInsuranceYearToDate}", formatter.format(deductions.getSocialInsuranceYearToDate()))
                .replace("{employeeCohesionFundForMonth}", formatter.format(deductions.getCohesionFundForMonth()))
                .replace("{employeeCohesionFundYearToDate}", formatter.format(deductions.getCohesionFundYearToDate()))
                .replace("{employeeIncomeTaxForMonth}", formatter.format(deductions.getIncomeTaxForMonth()))
                .replace("{employeeIncomeTaxYearToDate}", formatter.format(deductions.getIncomeTaxYearToDate()))
                .replace("{employeeNhsForMonth}", formatter.format(deductions.getNhsForMonth()))
                .replace("{employeeNhsYearToDate}", formatter.format(deductions.getNhsYearToDate()))
                .replace("{employeeTotalDeductions}", formatter.format(deductions.getTotalDeductionsForMonth()))
                .replace("{employeeTotalDeductionsYearToDate}", formatter.format(deductions.getTotalDeductionsYearToDate()));
    }

    /**
     * @param template An html template
     * @param payslip  A Payslip object
     * @return An html with all the employer deductions
     */
    private String addEmployerDeductions(String template, Payslip payslip) {
        DeductionsInfo deductions = payslip.getEmployerDeductionsInfo();
        BigDecimal totalEmployerCost = payslip.getEmployee().getGrossSalary()
                .add(deductions.getTotalDeductionsForMonth());

        return template.replace("{employerSocialInsuranceForMonth}", formatter.format(deductions.getSocialInsuranceForMonth()))
                .replace("{employerSocialInsuranceYearToDate}", formatter.format(deductions.getSocialInsuranceYearToDate()))
                .replace("{employerCohesionFundForMonth}", formatter.format(deductions.getCohesionFundForMonth()))
                .replace("{employerCohesionFundYearToDate}", formatter.format(deductions.getCohesionFundYearToDate()))
                .replace("{employerRedundancyFundForMonth}", formatter.format(deductions.getRedundancyFundForMonth()))
                .replace("{employerRedundancyFundYearToDate}", formatter.format(deductions.getRedundancyFundYearToDate()))
                .replace("{employerIndustrialTrainingForMonth}", formatter.format(deductions.getIndustrialTrainingForMonth()))
                .replace("{employerIndustrialTrainingYearToDate}", formatter.format(deductions.getIndustrialTrainingYearToDate()))
                .replace("{employerNhsForMonth}", formatter.format(deductions.getNhsForMonth()))
                .replace("{employerNhsYearToDate}", formatter.format(deductions.getNhsYearToDate()))
                .replace("{employerTotalContribution}", formatter.format(deductions.getTotalDeductionsForMonth()))
                .replace("{employerTotalContributionYearToDate}", formatter.format(deductions.getTotalDeductionsYearToDate()))
                .replace("{totalEmployerCost}", formatter.format(totalEmployerCost))
                .replace("{totalEmployerCostYearToDate}", NA);
    }

    /**
     * @param content  The content of the html file to be created
     * @param filename The name of the file to be created
     * @throws IOException If an I/O error occurs
     */
    private void createNewPayslip(String content, String filename) throws IOException {
        String outputDirPath = System.getProperty("user.dir") + File.separator + outputDirectory;
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            logger.info("Creating directory: " + outputDirPath);
            boolean wasDirectoryWithParentsCreated = outputDir.mkdirs();

            if (!wasDirectoryWithParentsCreated) {
                throw new IOException("Directory " + outputDirPath + " could not be created");
            }
        }

        String filepath = outputDir + File.separator + filename + ".html";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));

        writer.write(content);
        writer.close();
    }

    /**
     * @return An html template without payslip info
     */
    private String loadTemplateFile() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String filepath = rootPath + File.separator + templateFilename;

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filepath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }

        return contentBuilder.toString();
    }

    /**
     * @param employeeId The ID of the employee
     * @return The name of the file to be created
     */
    private String getPayslipFilename(String employeeId) {
        return employeeId + "_" + DateUtil.localDateToDateStr(DateUtil.now(), "yyyyMM");
    }
}
