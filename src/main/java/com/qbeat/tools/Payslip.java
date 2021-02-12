package com.qbeat.tools;

import com.qbeat.tools.models.CompanyInfo;
import com.qbeat.tools.models.DeductionsInfo;
import com.qbeat.tools.models.Employee;

public class Payslip {
    private final CompanyInfo companyInfo;
    private final Employee employee;
    private final DeductionsInfo employeeDeductionsInfo;
    private final DeductionsInfo employerDeductionsInfo;

    public Payslip(CompanyInfo companyInfo, Employee employee, DeductionsInfo employeeDeductionsInfo, DeductionsInfo employerDeductionsInfo) {
        this.companyInfo = companyInfo;
        this.employee = employee;
        this.employeeDeductionsInfo = employeeDeductionsInfo;
        this.employerDeductionsInfo = employerDeductionsInfo;
    }

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public Employee getEmployee() {
        return employee;
    }

    public DeductionsInfo getEmployeeDeductionsInfo() {
        return employeeDeductionsInfo;
    }

    public DeductionsInfo getEmployerDeductionsInfo() {
        return employerDeductionsInfo;
    }

    @Override
    public String toString() {
        return "Payslip{" +
                "companyInfo=" + companyInfo +
                ", employee=" + employee +
                ", employeeDeductionsInfo=" + employeeDeductionsInfo +
                ", employerDeductionsInfo=" + employerDeductionsInfo +
                '}';
    }
}
