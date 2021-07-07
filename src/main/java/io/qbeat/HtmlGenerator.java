package io.qbeat;

import io.qbeat.models.CompanyInfo;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class HtmlGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlGenerator.class);

    private static final String NA = "N/A";

    private final String templateFilename;
    private final List<Payslip> payslips;
    private final String outputDirectory;

    public HtmlGenerator(String templateFilename, List<Payslip> payslips, String outputDirectory) {
        this.templateFilename = templateFilename;
        this.payslips = payslips;
        this.outputDirectory = outputDirectory;
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
     * @param companyInfo A CompanyInfo object
     * @return An html with all the company info
     */
    private String addCompanyInfo(String template, CompanyInfo companyInfo) {
        return template.replace("{companyName}", companyInfo.getName())
                .replace("{companyAddress}", companyInfo.getAddress())
                .replace("{companyPhone}", companyInfo.getPhone());
    }

    /**
     * @param template An html template
     * @param payslip  A Payslip object
     * @return An html with all the employee info
     */
    private String addEmployeeInfo(String template, Payslip payslip) {
        Employee employee = payslip.getEmployee();
        double netSalary = employee.getGrossSalary() - payslip.getEmployeeDeductionsInfo().getTotalDeductionsForMonth();

        return template.replace("{employeeName}", employee.getFullName())
                .replace("{employeeId}", employee.getId())
                .replace("{employeeSocialInsuranceId}", employee.getSocialInsuranceId())
                .replace("{date}", DateUtil.localDateToDateStr(DateUtil.now(), "dd/MM/yyyy"))
                .replace("{month}", DateUtil.localDateToDateStr(DateUtil.now(), "MMMM yyyy"))
                .replace("{employeeGrossSalary}", DecimalUtil.strFormatted(employee.getGrossSalary()))
                .replace("{employeeTotalEarnings}", DecimalUtil.strFormatted(employee.getGrossSalary()))
                .replace("{employeeGrossSalaryYearToDate}", NA)
                .replace("{employeeTotalEarningsYearToDate}", NA)
                .replace("{employeeNetSalary}", DecimalUtil.strFormatted(netSalary));
    }

    /**
     * @param template   An html template
     * @param deductions A DeductionsInfo object of employee
     * @return An html with all the employee deductions
     */
    private String addEmployeeDeductions(String template, DeductionsInfo deductions) {
        return template.replace("{employeeSocialInsuranceForMonth}", DecimalUtil.strFormatted(deductions.getSocialInsuranceForMonth()))
                .replace("{employeeSocialInsuranceYearToDate}", DecimalUtil.strFormatted(deductions.getSocialInsuranceYearToDate()))
                .replace("{employeeCohesionFundForMonth}", DecimalUtil.strFormatted(deductions.getCohesionFundForMonth()))
                .replace("{employeeCohesionFundYearToDate}", DecimalUtil.strFormatted(deductions.getCohesionFundYearToDate()))
                .replace("{employeeIncomeTaxForMonth}", DecimalUtil.strFormatted(deductions.getIncomeTaxForMonth()))
                .replace("{employeeIncomeTaxYearToDate}", DecimalUtil.strFormatted(deductions.getIncomeTaxYearToDate()))
                .replace("{employeeNhsForMonth}", DecimalUtil.strFormatted(deductions.getNhsForMonth()))
                .replace("{employeeNhsYearToDate}", DecimalUtil.strFormatted(deductions.getNhsYearToDate()))
                .replace("{employeeTotalDeductions}", DecimalUtil.strFormatted(deductions.getTotalDeductionsForMonth()))
                .replace("{employeeTotalDeductionsYearToDate}", DecimalUtil.strFormatted(deductions.getTotalDeductionsYearToDate()));
    }

    /**
     * @param template An html template
     * @param payslip  A Payslip object
     * @return An html with all the employer deductions
     */
    private String addEmployerDeductions(String template, Payslip payslip) {
        DeductionsInfo deductions = payslip.getEmployerDeductionsInfo();
        double totalEmployerCost = payslip.getEmployee().getGrossSalary() + deductions.getTotalDeductionsForMonth();

        return template.replace("{employerSocialInsuranceForMonth}", DecimalUtil.strFormatted(deductions.getSocialInsuranceForMonth()))
                .replace("{employerSocialInsuranceYearToDate}", DecimalUtil.strFormatted(deductions.getSocialInsuranceYearToDate()))
                .replace("{employerCohesionFundForMonth}", DecimalUtil.strFormatted(deductions.getCohesionFundForMonth()))
                .replace("{employerCohesionFundYearToDate}", DecimalUtil.strFormatted(deductions.getCohesionFundYearToDate()))
                .replace("{employerRedundancyFundForMonth}", DecimalUtil.strFormatted(deductions.getRedundancyFundForMonth()))
                .replace("{employerRedundancyFundYearToDate}", DecimalUtil.strFormatted(deductions.getRedundancyFundYearToDate()))
                .replace("{employerIndustrialTrainingForMonth}", DecimalUtil.strFormatted(deductions.getIndustrialTrainingForMonth()))
                .replace("{employerIndustrialTrainingYearToDate}", DecimalUtil.strFormatted(deductions.getIndustrialTrainingYearToDate()))
                .replace("{employerNhsForMonth}", DecimalUtil.strFormatted(deductions.getNhsForMonth()))
                .replace("{employerNhsYearToDate}", DecimalUtil.strFormatted(deductions.getNhsYearToDate()))
                .replace("{employerTotalContribution}", DecimalUtil.strFormatted(deductions.getTotalDeductionsForMonth()))
                .replace("{employerTotalContributionYearToDate}", DecimalUtil.strFormatted(deductions.getTotalDeductionsYearToDate()))
                .replace("{totalEmployerCost}", DecimalUtil.strFormatted(totalEmployerCost))
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

            if (!wasDirectoryWithParentsCreated){
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
        return employeeId + "_" + DateUtil.localDateToDateStr(DateUtil.now(), "MM-yyyy");
    }
}
