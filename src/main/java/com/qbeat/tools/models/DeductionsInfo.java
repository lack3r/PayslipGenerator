package com.qbeat.tools.models;

import com.qbeat.tools.utils.DecimalUtil;

public class DeductionsInfo {
    private double socialInsuranceForMonth;
    private double socialInsuranceYearToDate;

    private double cohesionFundForMonth;
    private double cohesionFundYearToDate;

    private double incomeTaxForMonth;
    private double incomeTaxYearToDate;

    private double redundancyFundForMonth;
    private double redundancyFundYearToDate;

    private double industrialTrainingForMonth;
    private double industrialTrainingYearToDate;

    private double nhsForMonth;
    private double nhsYearToDate;

    public double getSocialInsuranceForMonth() {
        return socialInsuranceForMonth;
    }

    public void setSocialInsuranceForMonth(double socialInsuranceForMonth) {
        this.socialInsuranceForMonth = socialInsuranceForMonth;
    }

    public double getSocialInsuranceYearToDate() {
        return socialInsuranceYearToDate;
    }

    public void setSocialInsuranceYearToDate(double socialInsuranceYearToDate) {
        this.socialInsuranceYearToDate = socialInsuranceYearToDate;
    }

    public double getCohesionFundForMonth() {
        return cohesionFundForMonth;
    }

    public void setCohesionFundForMonth(double cohesionFundForMonth) {
        this.cohesionFundForMonth = cohesionFundForMonth;
    }

    public double getCohesionFundYearToDate() {
        return cohesionFundYearToDate;
    }

    public void setCohesionFundYearToDate(double cohesionFundYearToDate) {
        this.cohesionFundYearToDate = cohesionFundYearToDate;
    }

    public double getIncomeTaxForMonth() {
        return incomeTaxForMonth;
    }

    public void setIncomeTaxForMonth(double incomeTaxForMonth) {
        this.incomeTaxForMonth = incomeTaxForMonth;
    }

    public double getIncomeTaxYearToDate() {
        return incomeTaxYearToDate;
    }

    public void setIncomeTaxYearToDate(double incomeTaxYearToDate) {
        this.incomeTaxYearToDate = incomeTaxYearToDate;
    }

    public double getRedundancyFundForMonth() {
        return redundancyFundForMonth;
    }

    public void setRedundancyFundForMonth(double redundancyFundForMonth) {
        this.redundancyFundForMonth = redundancyFundForMonth;
    }

    public double getRedundancyFundYearToDate() {
        return redundancyFundYearToDate;
    }

    public void setRedundancyFundYearToDate(double redundancyFundYearToDate) {
        this.redundancyFundYearToDate = redundancyFundYearToDate;
    }

    public double getIndustrialTrainingForMonth() {
        return industrialTrainingForMonth;
    }

    public void setIndustrialTrainingForMonth(double industrialTrainingForMonth) {
        this.industrialTrainingForMonth = industrialTrainingForMonth;
    }

    public double getIndustrialTrainingYearToDate() {
        return industrialTrainingYearToDate;
    }

    public void setIndustrialTrainingYearToDate(double industrialTrainingYearToDate) {
        this.industrialTrainingYearToDate = industrialTrainingYearToDate;
    }

    public double getNhsForMonth() {
        return nhsForMonth;
    }

    public void setNhsForMonth(double nhsForMonth) {
        this.nhsForMonth = nhsForMonth;
    }

    public double getNhsYearToDate() {
        return nhsYearToDate;
    }

    public void setNhsYearToDate(double nhsYearToDate) {
        this.nhsYearToDate = nhsYearToDate;
    }

    public double getMonthlyNonTaxableAmount() {
        double amount = socialInsuranceForMonth + nhsForMonth;

        return DecimalUtil.round(amount);
    }

    public double getTotalDeductionsForMonth() {
        double total = socialInsuranceForMonth + cohesionFundForMonth + incomeTaxForMonth + redundancyFundForMonth +
                industrialTrainingForMonth + nhsForMonth;

        return DecimalUtil.round(total);
    }

    public double getTotalDeductionsYearToDate() {
        double total = socialInsuranceYearToDate + cohesionFundYearToDate + incomeTaxYearToDate +
                redundancyFundYearToDate + industrialTrainingYearToDate + nhsYearToDate;

        return DecimalUtil.round(total);
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
