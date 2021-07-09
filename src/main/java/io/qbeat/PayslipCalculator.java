package io.qbeat;

import io.qbeat.config.GeneralConfig;
import io.qbeat.models.PersonType;
import io.qbeat.config.TaxConfig;
import io.qbeat.models.Company;
import io.qbeat.models.Deductions;
import io.qbeat.models.Employee;
import io.qbeat.models.Payslip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PayslipCalculator {
    private static final Logger logger = LogManager.getLogger(PayslipCalculator.class);

    // TODO Hard-coded months
    private static final int MONTHS_TO_CONSIDER = 13;

    private final Company company;
    private final TaxConfig taxConfig;
    private final PayslipHistoryDAO payslipHistoryDAO;
    private final GeneralConfig generalConfig;

    public PayslipCalculator(Company company, TaxConfig taxConfig, GeneralConfig generalConfig, PayslipHistoryDAO payslipHistoryDAO) {
        this.company = company;
        this.taxConfig = taxConfig;
        this.generalConfig = generalConfig;
        this.payslipHistoryDAO = payslipHistoryDAO;
    }

    /**
     * @return A list of Payslip objects
     */
    public List<Payslip> calculate() {
        List<Payslip> payslips = new ArrayList<>();
        try {
            logger.debug("Total of " + company.getEmployees().size() + " payslip(s) info to be calculated");
            for (Employee employee : company.getEmployees()) {
                Payslip employeePayslip = calculateEmployeePayslip(employee);

                logger.info("Payslip info of employee with Id " + employee.getId() + " was successfully calculated");
                payslips.add(employeePayslip);
            }
        } catch (Exception e) {
            logger.error("Failed to generate payslip info of employees. Process aborted", e);
            throw e;
        }

        return payslips;
    }

    /**
     * @param employee The employee to calculate its payslip data
     * @return A Payslip object for the given employee
     */
    private Payslip calculateEmployeePayslip(Employee employee) {
        final TaxCalculator taxCalculator = new TaxCalculator(employee.getGrossSalary(), taxConfig.getProperties(),
                MONTHS_TO_CONSIDER);

        DeductionsCalculator employeeDeductionsCalculator = new DeductionsCalculator(PersonType.EMPLOYEE, employee,
                taxCalculator, generalConfig.getProperties(PersonType.EMPLOYEE), payslipHistoryDAO, MONTHS_TO_CONSIDER);
        DeductionsCalculator employerDeductionsCalculator = new DeductionsCalculator(PersonType.EMPLOYER, employee,
                taxCalculator, generalConfig.getProperties(PersonType.EMPLOYER), payslipHistoryDAO, MONTHS_TO_CONSIDER);

        Deductions employeeDeductions = employeeDeductionsCalculator.calculate();
        Deductions employerDeductions = employerDeductionsCalculator.calculate();

        return new Payslip(company, employee, employeeDeductions, employerDeductions);
    }
}
