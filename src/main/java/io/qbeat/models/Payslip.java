package io.qbeat.models;

import io.qbeat.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Payslip {
    private final Company company;
    private final Employee employee;
    private final Deductions employeeDeductions;
    private final Deductions employerDeductions;

    public Payslip(Company company, Employee employee, Deductions employeeDeductions, Deductions employerDeductions) {
        this.company = company;
        this.employee = employee;
        this.employeeDeductions = employeeDeductions;
        this.employerDeductions = employerDeductions;
    }

    public Company getCompanyInfo() {
        return company;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Deductions getEmployeeDeductionsInfo() {
        return employeeDeductions;
    }

    public Deductions getEmployerDeductionsInfo() {
        return employerDeductions;
    }

    /**
     * Gets a person type and returns a CSV line comma separated based on the info of given person type
     *
     * @param personType Type of person to determine which deductions info to use
     * @return A CSV line comma separated
     */
    public String toPayslipHistoryCSVLine(PersonType personType) {
        Deductions info = personType == PersonType.EMPLOYEE ? employeeDeductions : employerDeductions;
        List<String> data = new ArrayList<>();

        data.add(employee.getId());
        data.add(personType.getValue());
        data.add(DateUtil.localDateToDateStr(DateUtil.now(), "dd/MM/yyyy"));
        data.add(String.valueOf(info.getSocialInsuranceForMonth()));
        data.add(String.valueOf(info.getCohesionFundForMonth()));
        data.add(String.valueOf(info.getIncomeTaxForMonth()));
        data.add(String.valueOf(info.getRedundancyFundForMonth()));
        data.add(String.valueOf(info.getIndustrialTrainingForMonth()));
        data.add(String.valueOf(info.getNhsForMonth()));

        return data.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    @Override
    public String toString() {
        return "Payslip{" +
                "company=" + company +
                ", employee=" + employee +
                ", employeeDeductions=" + employeeDeductions +
                ", employerDeductions=" + employerDeductions +
                '}';
    }
}
