package io.qbeat;

import io.qbeat.models.PersonType;
import io.qbeat.models.DeductionsInfo;
import io.qbeat.models.Employee;
import io.qbeat.models.GeneralConfigProperty;
import io.qbeat.models.PayslipHistory;
import io.qbeat.utils.DecimalUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class DeductionsCalculator {

    private static final Logger logger = LogManager.getLogger(DeductionsCalculator.class);

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
        double socialInsuranceForMonth = calculateDeductionsForMonth(configProperties.get("SocialInsurance"));
        double totalSocialInsurancePaid = payslipHistories.stream()
                .mapToDouble(PayslipHistory::getSocialInsurance)
                .sum();
        double socialInsuranceYearToDate = DecimalUtil.round(totalSocialInsurancePaid + socialInsuranceForMonth);

        deductionsInfo.setSocialInsuranceForMonth(socialInsuranceForMonth);
        deductionsInfo.setSocialInsuranceYearToDate(socialInsuranceYearToDate);
    }

    private void calculateCohesionFund() {
        double cohesionFundForMonth = calculateDeductionsForMonth(configProperties.get("CohesionFund"));
        double totalCohesionFundPaid = payslipHistories.stream()
                .mapToDouble(PayslipHistory::getCohesionFund)
                .sum();
        double cohesionFundYearToDate = DecimalUtil.round(totalCohesionFundPaid + cohesionFundForMonth);

        deductionsInfo.setCohesionFundForMonth(cohesionFundForMonth);
        deductionsInfo.setCohesionFundYearToDate(cohesionFundYearToDate);
    }

    private void calculateRedundancyFund() {
        double redundancyFundForMonth = calculateDeductionsForMonth(configProperties.get("RedundancyFund"));
        double totalRedundancyFundPaid = payslipHistories.stream()
                .mapToDouble(PayslipHistory::getRedundancyFund)
                .sum();
        double redundancyFundYearToDate = DecimalUtil.round(totalRedundancyFundPaid + redundancyFundForMonth);

        deductionsInfo.setRedundancyFundForMonth(redundancyFundForMonth);
        deductionsInfo.setRedundancyFundYearToDate(redundancyFundYearToDate);
    }

    private void calculateIndustrialTraining() {
        double industrialTrainingForMonth = calculateDeductionsForMonth(configProperties.get("IndustrialTraining"));
        double totalIndustrialTrainingPaid = payslipHistories.stream()
                .mapToDouble(PayslipHistory::getIndustrialTraining)
                .sum();
        double industrialTrainingYearToDate = DecimalUtil.round(totalIndustrialTrainingPaid + industrialTrainingForMonth);

        deductionsInfo.setIndustrialTrainingForMonth(industrialTrainingForMonth);
        deductionsInfo.setIndustrialTrainingYearToDate(industrialTrainingYearToDate);
    }

    private void calculateNHS() {
        double nhsForMonth = calculateDeductionsForMonth(configProperties.get("NHS"));
        double totalNhsPaid = payslipHistories.stream()
                .mapToDouble(PayslipHistory::getNhs)
                .sum();
        double nhsYearToDate = DecimalUtil.round(totalNhsPaid + nhsForMonth);

        deductionsInfo.setNhsForMonth(nhsForMonth);
        deductionsInfo.setNhsYearToDate(nhsYearToDate);
    }

    private void calculateTaxDeductions() {
        double incomeTaxForMonth = taxCalculator.calculate(deductionsInfo.getMonthlyNonTaxableAmount());
        double totalIncomeTaxPaid = payslipHistories.stream()
                .mapToDouble(PayslipHistory::getIncomeTax)
                .sum();
        double incomeTaxYearToDate = DecimalUtil.round(totalIncomeTaxPaid + incomeTaxForMonth);

        deductionsInfo.setIncomeTaxForMonth(incomeTaxForMonth);
        deductionsInfo.setIncomeTaxYearToDate(incomeTaxYearToDate);
    }

    private double calculateDeductionsForMonth(GeneralConfigProperty property) {
        // TODO: aloizou 07/07/21 MaxContributions are not yet considered in calculations
//        if (hasReachedMaxContributions(property)) {
//            return DecimalUtil.round(property.getMaxContributions() * property.getContributionsPercentage() / 100.0/ monthsToConsider);
//        }

        return DecimalUtil.round(employee.getGrossSalary() * property.getContributionsPercentage() / 100.0);
    }

    private boolean hasReachedMaxContributions(GeneralConfigProperty property) {
        return property.hasMaxContributions() && employee.getGrossSalary() * monthsToConsider > property.getMaxContributions();
    }
}
