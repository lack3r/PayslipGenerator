package io.qbeat;

import io.qbeat.config.GeneralConfig;
import io.qbeat.models.PersonType;
import io.qbeat.config.TaxConfig;
import io.qbeat.models.CompanyInfo;
import io.qbeat.models.DeductionsInfo;
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

    private final CompanyInfo companyInfo;
    private final TaxConfig taxConfig;
    private final PayslipHistoryDAO payslipHistoryDAO;
    private final GeneralConfig generalConfig;

    public PayslipCalculator(CompanyInfo companyInfo, TaxConfig taxConfig, GeneralConfig generalConfig, PayslipHistoryDAO payslipHistoryDAO) {
        this.companyInfo = companyInfo;
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
            logger.debug("Total of " + companyInfo.getEmployees().size() + " payslip(s) info to be calculated");
            for (Employee employee : companyInfo.getEmployees()) {
                Payslip employeePayslip = calculateEmployeePayslip(employee);

                logger.info("Payslip info of employee with Id " + employee.getId() + " was successfully calculated");
                payslips.add(employeePayslip);
            }
        }
        catch (Exception e) {
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

        DeductionsInfo employeeDeductionsInfo = employeeDeductionsCalculator.calculate();
        DeductionsInfo employerDeductionsInfo = employerDeductionsCalculator.calculate();

        return new Payslip(companyInfo, employee, employeeDeductionsInfo, employerDeductionsInfo);
    }
}
