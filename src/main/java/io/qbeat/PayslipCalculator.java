package io.qbeat;

import io.qbeat.config.AppConfig;
import io.qbeat.config.PersonType;
import io.qbeat.models.CompanyInfo;
import io.qbeat.models.DeductionsInfo;
import io.qbeat.models.Employee;
import io.qbeat.models.Payslip;

import java.util.ArrayList;
import java.util.List;

public class PayslipCalculator {
    // TODO Hard-coded months
    private static final int MONTHS_TO_CONSIDER = 13;

    private final CompanyInfo companyInfo;
    private final AppConfig appConfig;
    private final PayslipHistoryDAO payslipHistoryDAO;

    public PayslipCalculator(CompanyInfo companyInfo, AppConfig appConfig, PayslipHistoryDAO payslipHistoryDAO) {
        this.companyInfo = companyInfo;
        this.appConfig = appConfig;
        this.payslipHistoryDAO = payslipHistoryDAO;
    }

    /**
     * @return A list of Payslip objects
     */
    public List<Payslip> calculate() {
        List<Payslip> payslips = new ArrayList<>();
        try {
            System.out.println("Total of " + companyInfo.getEmployees().size() + " payslip(s) info to be calculated");
            for (Employee employee : companyInfo.getEmployees()) {
                Payslip employeePayslip = calculateEmployeePayslip(employee);

                System.out.println("Payslip info of employee with Id " + employee.getId() + " successfully calculated");
                payslips.add(employeePayslip);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to generate payslip info of employees. Process aborted");
            e.printStackTrace();
            throw e;
        }

        return payslips;
    }

    /**
     * @param employee The employee to calculate its payslip data
     * @return A Payslip object for the given employee
     */
    private Payslip calculateEmployeePayslip(Employee employee) {
        final TaxCalculator taxCalculator = new TaxCalculator(employee.getGrossSalary(), appConfig.getTaxProperties(),
                MONTHS_TO_CONSIDER);

        DeductionsCalculator employeeDeductionsCalculator = new DeductionsCalculator(PersonType.EMPLOYEE, employee,
                taxCalculator, appConfig.getGeneralProperties(PersonType.EMPLOYEE), payslipHistoryDAO, MONTHS_TO_CONSIDER);
        DeductionsCalculator employerDeductionsCalculator = new DeductionsCalculator(PersonType.EMPLOYER, employee,
                taxCalculator, appConfig.getGeneralProperties(PersonType.EMPLOYER), payslipHistoryDAO, MONTHS_TO_CONSIDER);

        DeductionsInfo employeeDeductionsInfo = employeeDeductionsCalculator.calculate();
        DeductionsInfo employerDeductionsInfo = employerDeductionsCalculator.calculate();

        return new Payslip(companyInfo, employee, employeeDeductionsInfo, employerDeductionsInfo);
    }
}