package io.qbeat;

import io.qbeat.models.TaxConfigProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TaxCalculator {
    private final int monthsToConsider;
    private final List<TaxConfigProperty> configProperties;
    private BigDecimal employeeYearlyTaxableSalary;
    private static final int DECIMALS = 2;

    public TaxCalculator(List<TaxConfigProperty> configProperties, int monthsToConsider) {
        this.configProperties = configProperties;
        this.monthsToConsider = monthsToConsider;
    }

    public BigDecimal calculate(BigDecimal employeeMonthlySalary, BigDecimal monthlyNonTaxableAmount) {
        computeEmployeeYearlyTaxableSalary(employeeMonthlySalary, monthlyNonTaxableAmount);

        BigDecimal tax = BigDecimal.ZERO;
        for (TaxConfigProperty property : configProperties) {
            tax = tax.add(calculateTaxRangeDeductions(property));
        }

        return tax;
    }

    private BigDecimal calculateTaxRangeDeductions(TaxConfigProperty taxProperty) {
        if (!shouldPayTax(taxProperty)) {
            return BigDecimal.ZERO;
        }

        // When employee's salary is more than the ending price of the particular tax range
        BigDecimal rate = taxProperty.getRate().divide(BigDecimal.valueOf(100.0), DECIMALS, RoundingMode.HALF_UP);
        if (employeeYearlyTaxableSalary.compareTo(BigDecimal.valueOf(taxProperty.getRangeEndPrice())) >= 0) {
            int rangeDifference = taxProperty.getRangeEndPrice() - taxProperty.getRangeStartPrice();
            return BigDecimal.valueOf(rangeDifference).multiply(rate).divide(BigDecimal.valueOf(monthsToConsider), DECIMALS, RoundingMode.HALF_UP);
        }
        BigDecimal salaryToBeTaxed = employeeYearlyTaxableSalary.subtract(BigDecimal.valueOf(taxProperty.getRangeStartPrice()));
        BigDecimal yearlyEstimatedTaxAmount = salaryToBeTaxed.multiply(rate);

        // Tax for the month
        return yearlyEstimatedTaxAmount.divide(BigDecimal.valueOf(monthsToConsider), DECIMALS, RoundingMode.HALF_UP);
    }

    private boolean shouldPayTax(TaxConfigProperty property) {
        return property.getRate().compareTo(BigDecimal.ZERO) > 0 && employeeYearlyTaxableSalary.compareTo(BigDecimal.valueOf(property.getRangeStartPrice())) >= 0;
    }

    private void computeEmployeeYearlyTaxableSalary(BigDecimal employeeMonthlySalary, BigDecimal monthlyNonTaxableAmount) {
        final BigDecimal employeeYearlySalary = employeeMonthlySalary.multiply(BigDecimal.valueOf(monthsToConsider));
        final BigDecimal yearlyNonTaxableAmount = monthlyNonTaxableAmount.multiply(BigDecimal.valueOf(monthsToConsider));
        employeeYearlyTaxableSalary = employeeYearlySalary.subtract(yearlyNonTaxableAmount);
    }
}
