package io.qbeat;

import io.qbeat.models.*;
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
    private final Map<String, GeneralConfigProperty> configProperties;
    private final TaxCalculator taxCalculator;
    private final PayslipHistoryDAO payslipHistoryDAO;
    private final int monthsToConsider;

    private final Deductions deductions = new Deductions();
    private List<PayslipHistory> payslipHistories;

    public DeductionsCalculator(PersonType personType,
                                Map<String, GeneralConfigProperty> configProperties, TaxCalculator taxCalculator,
                                PayslipHistoryDAO payslipHistoryDAO, int monthsToConsider) {
        this.personType = personType;
        this.configProperties = configProperties;
        this.taxCalculator = taxCalculator;
        this.payslipHistoryDAO = payslipHistoryDAO;
        this.monthsToConsider = monthsToConsider;
    }

    /**
     * @return The Deductions based on the person type
     */
    Deductions calculate(Employee employee) {
        payslipHistories = payslipHistoryDAO.findByEmployeeIdAndPersonType(employee.getId(), personType);

        switch (personType) {
            case EMPLOYEE:
                calculateForEmployee(employee.getGrossSalary());
                break;
            case EMPLOYER:
                calculateForEmployer(employee.getGrossSalary());
                break;
            default:
                logger.error("Unable to calculate deductions. Unknown person type: {}", personType);
        }

        return deductions;
    }

    /**
     * Calculates the Deductions of the employee
     */
    private void calculateForEmployee(BigDecimal grossSalary) {
        calculateForAnyone(grossSalary);

        // Tax deductions must be calculated after calculating
        // non taxable deductions (i.e social insurance, nhs, etc.)
        calculateTaxDeductions(grossSalary);
    }

    /**
     * Calculates the Deductions of the employer
     */
    private void calculateForEmployer(BigDecimal grossSalary) {
        calculateForAnyone(grossSalary);
        calculateRedundancyFund(grossSalary);
        calculateIndustrialTraining(grossSalary);
    }

    /**
     * Common Deductions calculations
     */
    private void calculateForAnyone(BigDecimal grossSalary) {
        calculateSocialInsurance(grossSalary);
        calculateCohesionFund(grossSalary);
        calculateNHS(grossSalary);
    }

    private void calculateSocialInsurance(BigDecimal grossSalary) {
        BigDecimal socialInsuranceForMonth = calculateDeductionsForMonth(grossSalary, configProperties.get("SocialInsurance"));
        BigDecimal totalSocialInsurancePaid = payslipHistories.stream()
                .map(PayslipHistory::getSocialInsurance)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal socialInsuranceYearToDate = totalSocialInsurancePaid.add(socialInsuranceForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductions.setSocialInsuranceForMonth(socialInsuranceForMonth);
        deductions.setSocialInsuranceYearToDate(socialInsuranceYearToDate);
    }

    private void calculateCohesionFund(BigDecimal grossSalary) {
        BigDecimal cohesionFundForMonth = calculateDeductionsForMonth(grossSalary, configProperties.get("CohesionFund"));
        BigDecimal totalCohesionFundPaid = payslipHistories.stream()
                .map(PayslipHistory::getCohesionFund)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal cohesionFundYearToDate = totalCohesionFundPaid.add(cohesionFundForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductions.setCohesionFundForMonth(cohesionFundForMonth);
        deductions.setCohesionFundYearToDate(cohesionFundYearToDate);
    }

    private void calculateRedundancyFund(BigDecimal grossSalary) {
        BigDecimal redundancyFundForMonth = calculateDeductionsForMonth(grossSalary, configProperties.get("RedundancyFund"));
        BigDecimal totalRedundancyFundPaid = payslipHistories.stream()
                .map(PayslipHistory::getRedundancyFund)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal redundancyFundYearToDate = totalRedundancyFundPaid.add(redundancyFundForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductions.setRedundancyFundForMonth(redundancyFundForMonth);
        deductions.setRedundancyFundYearToDate(redundancyFundYearToDate);
    }

    private void calculateIndustrialTraining(BigDecimal grossSalary) {
        BigDecimal industrialTrainingForMonth = calculateDeductionsForMonth(grossSalary, configProperties.get("IndustrialTraining"));
        BigDecimal totalIndustrialTrainingPaid = payslipHistories.stream()
                .map(PayslipHistory::getIndustrialTraining)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal industrialTrainingYearToDate = totalIndustrialTrainingPaid.add(industrialTrainingForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);

        deductions.setIndustrialTrainingForMonth(industrialTrainingForMonth);
        deductions.setIndustrialTrainingYearToDate(industrialTrainingYearToDate);
    }

    private void calculateNHS(BigDecimal grossSalary) {
        BigDecimal nhsForMonth = calculateDeductionsForMonth(grossSalary, configProperties.get("NHS"));
        BigDecimal totalNhsPaid = payslipHistories.stream()
                .map(PayslipHistory::getNhs)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal nhsYearToDate = totalNhsPaid.add(nhsForMonth);

        deductions.setNhsForMonth(nhsForMonth);
        deductions.setNhsYearToDate(nhsYearToDate);
    }

    private void calculateTaxDeductions(BigDecimal grossSalary) {
        BigDecimal incomeTaxForMonth = taxCalculator.calculate(grossSalary, deductions.getMonthlyNonTaxableAmount());
        BigDecimal totalIncomeTaxPaid = payslipHistories.stream()
                .map(PayslipHistory::getIncomeTax)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        BigDecimal incomeTaxYearToDate = totalIncomeTaxPaid.add(incomeTaxForMonth);

        deductions.setIncomeTaxForMonth(incomeTaxForMonth);
        deductions.setIncomeTaxYearToDate(incomeTaxYearToDate);
    }

    private BigDecimal calculateDeductionsForMonth(BigDecimal grossSalary, GeneralConfigProperty property) {
        // TODO: aloizou 07/07/21 MaxContributions are not yet considered in calculations
//        if (hasReachedMaxContributions(property)) {
//            return DecimalUtil.round(property.getMaxContributions() * property.getContributionsPercentage() / 100.0/ monthsToConsider);
//        }

        return grossSalary.multiply(property.getContributionsPercentage()).divide(BigDecimal.valueOf(100.0), DECIMALS, RoundingMode.HALF_UP);
    }

    // TODO: aloizou 07/07/21 MaxContributions are not yet considered in calculations
    private boolean hasReachedMaxContributions(BigDecimal grossSalary, GeneralConfigProperty property) {
        BigDecimal contributions = grossSalary.multiply(BigDecimal.valueOf(monthsToConsider));
        return property.hasMaxContributions() &&  contributions.compareTo(property.getMaxContributions()) > 0;
    }
}
