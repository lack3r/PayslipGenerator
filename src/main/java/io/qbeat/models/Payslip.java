package io.qbeat.models;

import io.qbeat.config.PersonType;
import io.qbeat.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Gets a person type and returns a CSV line comma separated based on the info of given person type
     *
     * @param personType Type of person to determine which deductions info to use
     * @return A CSV line comma separated
     */
    public String toPayslipHistoryCSVLine(PersonType personType) {
        DeductionsInfo info = personType == PersonType.EMPLOYEE ? employeeDeductionsInfo : employerDeductionsInfo;
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
                "companyInfo=" + companyInfo +
                ", employee=" + employee +
                ", employeeDeductionsInfo=" + employeeDeductionsInfo +
                ", employerDeductionsInfo=" + employerDeductionsInfo +
                '}';
    }
}
