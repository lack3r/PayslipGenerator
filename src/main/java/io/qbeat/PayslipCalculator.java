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

    DeductionsCalculator employeeDeductionsCalculator;
    DeductionsCalculator employerDeductionsCalculator;

    public PayslipCalculator(Company company, TaxConfig taxConfig, GeneralConfig generalConfig, DeductionsCalculator employeeDeductionsCalculator, DeductionsCalculator employerDeductionsCalculator, PayslipHistoryDAO payslipHistoryDAO) {
        this.company = company;
        this.taxConfig = taxConfig;

        this.employeeDeductionsCalculator = employeeDeductionsCalculator;
        this.employerDeductionsCalculator = employerDeductionsCalculator;
    }

    /**
     * @return A list of Payslip objects
     */
    public List<Payslip> calculate() {
        List<Payslip> payslips = new ArrayList<>();
        try {
            logger.debug("Total of {} payslip(s) info to be calculated", company.getEmployees().size());
            for (Employee employee : company.getEmployees()) {
                Payslip employeePayslip = calculateEmployeePayslip(employee);

                logger.info("Payslip info of employee with Id {} was successfully calculated", employee.getId());
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

        Deductions employeeDeductions = employeeDeductionsCalculator.calculate(employee);
        Deductions employerDeductions = employerDeductionsCalculator.calculate(employee);

        return new Payslip(company, employee, employeeDeductions, employerDeductions);
    }
}
