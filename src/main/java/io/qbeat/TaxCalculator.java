package io.qbeat;

import io.qbeat.models.TaxConfigProperty;
import io.qbeat.utils.DecimalUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

public class TaxCalculator {
    private final int monthsToConsider;
    private final BigDecimal employeeMonthlySalary;
    private final List<TaxConfigProperty> configProperties;
    private BigDecimal employeeYearlyTaxableSalary;
    private static final MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);

    public TaxCalculator(BigDecimal employeeMonthlySalary, List<TaxConfigProperty> configProperties, int monthsToConsider) {
        this.employeeMonthlySalary = employeeMonthlySalary;
        this.configProperties = configProperties;
        this.monthsToConsider = monthsToConsider;
    }

    public BigDecimal calculate(BigDecimal monthlyNonTaxableAmount) {
        computeEmployeeYearlyTaxableSalary(monthlyNonTaxableAmount);

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
        BigDecimal rate = taxProperty.getRate().divide(BigDecimal.valueOf(100.0), mathContext);
        if (employeeYearlyTaxableSalary.compareTo(BigDecimal.valueOf(taxProperty.getRangeEndPrice())) >= 0) {
            int rangeDifference = taxProperty.getRangeEndPrice() - taxProperty.getRangeStartPrice();
            return BigDecimal.valueOf(rangeDifference).multiply(rate).divide(BigDecimal.valueOf(monthsToConsider), mathContext);
        }
        BigDecimal salaryToBeTaxed = employeeYearlyTaxableSalary.subtract(BigDecimal.valueOf(taxProperty.getRangeStartPrice()));
        BigDecimal yearlyEstimatedTaxAmount = salaryToBeTaxed.multiply(rate);

        // Tax for the month
        return yearlyEstimatedTaxAmount.divide(BigDecimal.valueOf(monthsToConsider),mathContext );
    }

    private boolean shouldPayTax(TaxConfigProperty property) {
        return property.getRate().compareTo(BigDecimal.ZERO) > 0 && employeeYearlyTaxableSalary.compareTo(BigDecimal.valueOf(property.getRangeStartPrice())) >= 0;
    }

    private void computeEmployeeYearlyTaxableSalary(BigDecimal monthlyNonTaxableAmount) {
        final BigDecimal employeeYearlySalary = employeeMonthlySalary.multiply(BigDecimal.valueOf(monthsToConsider));
        final BigDecimal yearlyNonTaxableAmount = monthlyNonTaxableAmount.multiply(BigDecimal.valueOf(monthsToConsider));
        employeeYearlyTaxableSalary = employeeYearlySalary.subtract(yearlyNonTaxableAmount);
    }
}
