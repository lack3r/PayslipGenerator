package io.qbeat;

import io.qbeat.models.Company;
import io.qbeat.models.Deductions;
import io.qbeat.models.Employee;
import io.qbeat.models.Payslip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PayslipCalculator {
    private static final Logger logger = LogManager.getLogger(PayslipCalculator.class);

    private DeductionsCalculator employeeDeductionsCalculator;
    private DeductionsCalculator employerDeductionsCalculator;

    @Autowired
    public PayslipCalculator(@Qualifier("employeeDeductionsCalculator") DeductionsCalculator employeeDeductionsCalculator,
                             @Qualifier("employerDeductionsCalculator") DeductionsCalculator employerDeductionsCalculator) {
        this.employeeDeductionsCalculator = employeeDeductionsCalculator;
        this.employerDeductionsCalculator = employerDeductionsCalculator;
    }

    /**
     * @return A list of Payslip objects
     */
    List<Payslip> calculate(Company company) {
        List<Payslip> payslips = new ArrayList<>();
        try {
            logger.debug("Total of {} payslip(s) info to be calculated", company.getEmployees().size());
            for (Employee employee : company.getEmployees()) {
                Payslip employeePayslip = calculateEmployeePayslip(company, employee);

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
    private Payslip calculateEmployeePayslip(Company company, Employee employee) {

        Deductions employeeDeductions = employeeDeductionsCalculator.calculate(employee);
        Deductions employerDeductions = employerDeductionsCalculator.calculate(employee);

        return new Payslip(company, employee, employeeDeductions, employerDeductions);
    }
}
