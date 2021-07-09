package io.qbeat.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DeductionsInfo {

    private static final int DECIMALS = 2;

    @Getter
    @Setter
    private BigDecimal socialInsuranceForMonth = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal socialInsuranceYearToDate = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal cohesionFundForMonth = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal cohesionFundYearToDate = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal incomeTaxForMonth = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal incomeTaxYearToDate = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal redundancyFundForMonth = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal redundancyFundYearToDate = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal industrialTrainingForMonth = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal industrialTrainingYearToDate = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal nhsForMonth = BigDecimal.ZERO;

    @Getter
    @Setter
    private BigDecimal nhsYearToDate = BigDecimal.ZERO;

    public BigDecimal getMonthlyNonTaxableAmount() {
        return socialInsuranceForMonth.add(nhsForMonth).setScale(DECIMALS, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalDeductionsForMonth() {
        return socialInsuranceForMonth
                .add(cohesionFundForMonth)
                .add(incomeTaxForMonth)
                .add(redundancyFundForMonth)
                .add(industrialTrainingForMonth)
                .add(nhsForMonth)
                .setScale(DECIMALS, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalDeductionsYearToDate() {
        return socialInsuranceYearToDate
                .add(cohesionFundYearToDate)
                .add(incomeTaxYearToDate)
                .add(redundancyFundYearToDate)
                .add(industrialTrainingYearToDate)
                .add(nhsYearToDate)
                .setScale(DECIMALS, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "DeductionsInfo{" +
                "socialInsuranceForMonth=" + socialInsuranceForMonth +
                ", socialInsuranceYearToDate=" + socialInsuranceYearToDate +
                ", cohesionFundForMonth=" + cohesionFundForMonth +
                ", cohesionFundYearToDate=" + cohesionFundYearToDate +
                ", incomeTaxForMonth=" + incomeTaxForMonth +
                ", incomeTaxYearToDate=" + incomeTaxYearToDate +
                ", redundancyFundForMonth=" + redundancyFundForMonth +
                ", redundancyFundYearToDate=" + redundancyFundYearToDate +
                ", industrialTrainingForMonth=" + industrialTrainingForMonth +
                ", industrialTrainingYearToDate=" + industrialTrainingYearToDate +
                ", nhsForMonth=" + nhsForMonth +
                ", nhsYearToDate=" + nhsYearToDate +
                ", monthlyNonTaxableAmount=" + getMonthlyNonTaxableAmount() +
                ", totalDeductionsForMonth=" + getTotalDeductionsForMonth() +
                ", totalDeductionsYearToDate=" + getTotalDeductionsYearToDate() +
                '}';
    }
}
