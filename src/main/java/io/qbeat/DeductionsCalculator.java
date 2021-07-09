package io.qbeat;

import io.qbeat.models.PersonType;
import io.qbeat.models.DeductionsInfo;
import io.qbeat.models.Employee;
import io.qbeat.models.GeneralConfigProperty;
import io.qbeat.models.PayslipHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class DeductionsCalculator {

    private static final Logger logger = LogManager.getLogger(DeductionsCalculator.class);
    private static final int DECIMALS = 2;

    private final PersonType personType;
    private final Employee employee;
    private final TaxCalculator taxCalculator;
    private final Map<String, GeneralConfigProperty> configProperties;
    private final PayslipHistoryDAO payslipHistoryDAO;
    private final int monthsToConsider;

    private final DeductionsInfo deductionsInfo = new DeductionsInfo();
    private List<PayslipHistory> payslipHistories;
    public DeductionsCalculator(PersonType personType, Employee employee, TaxCalculator taxCalculator,
                                Map<String, GeneralConfigProperty> configProperties,
                                PayslipHistoryDAO payslipHistoryDAO, int monthsToConsider) {
        this.personType = personType;
        this.employee = employee;
        this.taxCalculator = taxCalculator;
        this.configProperties = configProperties;
        this.payslipHistoryDAO = payslipHistoryDAO;
        this.monthsToConsider = monthsToConsider;
    }

    /**
     * @return The DeductionsInfo based on the person type
     */
    public DeductionsInfo calculate() {
        payslipHistories = payslipHistoryDAO.findByEmployeeIdAndPersonType(employee.getId(), personType);

        switch (personType) {
            case EMPLOYEE:
                calculateForEmployee();
                break;
            case EMPLOYER:
                calculateForEmployer();
                break;
            default:
                logger.error("Unable to calculate deductions. Unknown person type: " + personType);
        }

        return deductionsInfo;
    }

    /**
     * Calculates the DeductionsInfo of the employee
     */
    private void calculateForEmployee() {
        calculateForAnyone();

        // Tax deductions must be calculated after calculating
        // non taxable deductions (i.e social insurance, nhs, etc.)
        calculateTaxDeductions();
    }

    /**
     * Calculates the DeductionsInfo of the employer
     */
    private void calculateForEmployer() {
        calculateForAnyone();
        calculateRedundancyFund();
        calculateIndustrialTraining();
    }

    /**
     * Common DeductionsInfo calculations
     */
    private void calculateForAnyone() {
        calculateSocialInsurance();
        calculateCohesionFund();
        calculateNHS();
    }

    private void calculateSocialInsurance() {
        BigDecimal socialInsuranceForMonth = calculateDeductionsForMonth(configProperties.get("SocialInsurance"));
        BigDecimal totalSocialInsurancePaid = payslipHistories.stream()
                .map(PayslipHistory::getSocialInsurance)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal socialInsuranceYearToDate = totalSocialInsurancePaid.add(socialInsuranceForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductionsInfo.setSocialInsuranceForMonth(socialInsuranceForMonth);
        deductionsInfo.setSocialInsuranceYearToDate(socialInsuranceYearToDate);
    }

    private void calculateCohesionFund() {
        BigDecimal cohesionFundForMonth = calculateDeductionsForMonth(configProperties.get("CohesionFund"));
        BigDecimal totalCohesionFundPaid = payslipHistories.stream()
                .map(PayslipHistory::getCohesionFund)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal cohesionFundYearToDate = totalCohesionFundPaid.add(cohesionFundForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductionsInfo.setCohesionFundForMonth(cohesionFundForMonth);
        deductionsInfo.setCohesionFundYearToDate(cohesionFundYearToDate);
    }

    private void calculateRedundancyFund() {
        BigDecimal redundancyFundForMonth = calculateDeductionsForMonth(configProperties.get("RedundancyFund"));
        BigDecimal totalRedundancyFundPaid = payslipHistories.stream()
                .map(PayslipHistory::getRedundancyFund)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal redundancyFundYearToDate = totalRedundancyFundPaid.add(redundancyFundForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductionsInfo.setRedundancyFundForMonth(redundancyFundForMonth);
        deductionsInfo.setRedundancyFundYearToDate(redundancyFundYearToDate);
    }

    private void calculateIndustrialTraining() {
        BigDecimal industrialTrainingForMonth = calculateDeductionsForMonth(configProperties.get("IndustrialTraining"));
        BigDecimal totalIndustrialTrainingPaid = payslipHistories.stream()
                .map(PayslipHistory::getIndustrialTraining)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal industrialTrainingYearToDate = totalIndustrialTrainingPaid.add(industrialTrainingForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductionsInfo.setIndustrialTrainingForMonth(industrialTrainingForMonth);
        deductionsInfo.setIndustrialTrainingYearToDate(industrialTrainingYearToDate);
    }

    private void calculateNHS() {
        BigDecimal nhsForMonth = calculateDeductionsForMonth(configProperties.get("NHS"));
        BigDecimal totalNhsPaid = payslipHistories.stream()
                .map(PayslipHistory::getNhs)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal nhsYearToDate = totalNhsPaid.add(nhsForMonth);

        deductionsInfo.setNhsForMonth(nhsForMonth);
        deductionsInfo.setNhsYearToDate(nhsYearToDate);
    }

    private void calculateTaxDeductions() {
        BigDecimal incomeTaxForMonth = taxCalculator.calculate(deductionsInfo.getMonthlyNonTaxableAmount());
        BigDecimal totalIncomeTaxPaid = payslipHistories.stream()
                .map(PayslipHistory::getIncomeTax)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal incomeTaxYearToDate = totalIncomeTaxPaid.add(incomeTaxForMonth);

        deductionsInfo.setIncomeTaxForMonth(incomeTaxForMonth);
        deductionsInfo.setIncomeTaxYearToDate(incomeTaxYearToDate);
    }

    private BigDecimal calculateDeductionsForMonth(GeneralConfigProperty property) {
        // TODO: aloizou 07/07/21 MaxContributions are not yet considered in calculations
//        if (hasReachedMaxContributions(property)) {
//            return DecimalUtil.round(property.getMaxContributions() * property.getContributionsPercentage() / 100.0/ monthsToConsider);
//        }

        return employee.getGrossSalary().multiply(property.getContributionsPercentage()).divide(BigDecimal.valueOf(100.0), DECIMALS, RoundingMode.HALF_UP);
    }

    private boolean hasReachedMaxContributions(GeneralConfigProperty property) {
        BigDecimal contributions = employee.getGrossSalary().multiply(BigDecimal.valueOf(monthsToConsider));
        return property.hasMaxContributions() &&  contributions.compareTo(property.getMaxContributions()) > 0;
    }
}
