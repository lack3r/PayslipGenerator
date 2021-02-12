package com.qbeat.tools;

import com.qbeat.tools.config.AppConfig;
import com.qbeat.tools.config.PersonType;
import com.qbeat.tools.models.CompanyInfo;
import com.qbeat.tools.models.DeductionsInfo;
import com.qbeat.tools.models.Employee;

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
