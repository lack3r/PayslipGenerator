package io.qbeat;

import io.qbeat.models.TaxConfigProperty;
import io.qbeat.utils.DecimalUtil;

import java.util.List;

public class TaxCalculator {
    private final int monthsToConsider;
    private final double employeeMonthlySalary;
    private final List<TaxConfigProperty> configProperties;
    private double employeeYearlyTaxableSalary;

    public TaxCalculator(double employeeMonthlySalary, List<TaxConfigProperty> configProperties, int monthsToConsider) {
        this.employeeMonthlySalary = employeeMonthlySalary;
        this.configProperties = configProperties;
        this.monthsToConsider = monthsToConsider;
    }

    public double calculate(double monthlyNonTaxableAmount) {
        computeEmployeeYearlyTaxableSalary(monthlyNonTaxableAmount);

        double tax = 0.0;
        for (TaxConfigProperty property : configProperties) {
            tax += calculateTaxRangeDeductions(property);
        }

        return DecimalUtil.round(tax);
    }

    private double calculateTaxRangeDeductions(TaxConfigProperty taxProperty) {
        if (!shouldPayTax(taxProperty)) {
            return 0.0;
        }

        // When employee's salary is more than the ending price of the particular tax range
        if (employeeYearlyTaxableSalary >= taxProperty.getRangeEndPrice()) {
            return (taxProperty.getRangeEndPrice() - taxProperty.getRangeStartPrice()) * taxProperty.getRate() / 100.0 / monthsToConsider;
        }

        return (employeeYearlyTaxableSalary - taxProperty.getRangeStartPrice()) * taxProperty.getRate() / 100.0 / monthsToConsider;
    }

    private boolean shouldPayTax(TaxConfigProperty property) {
        return property.getRate() > 0 && employeeYearlyTaxableSalary >= property.getRangeStartPrice();
    }

    private void computeEmployeeYearlyTaxableSalary(double monthlyNonTaxableAmount) {
        final double employeeYearlySalary = employeeMonthlySalary * monthsToConsider;
        final double yearlyNonTaxableAmount = monthlyNonTaxableAmount * monthsToConsider;
        employeeYearlyTaxableSalary = employeeYearlySalary - yearlyNonTaxableAmount;
    }
}
