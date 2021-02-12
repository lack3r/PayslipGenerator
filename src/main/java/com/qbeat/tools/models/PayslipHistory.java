package com.qbeat.tools.models;

import com.qbeat.tools.config.CSVReader;
import com.qbeat.tools.config.PersonType;
import com.qbeat.tools.utils.DateUtil;

import java.time.LocalDate;
import java.util.List;

public class PayslipHistory {
    private String employeeId;
    private PersonType personType;
    private LocalDate date;
    private double socialInsurance;
    private double cohesionFund;
    private double incomeTax;
    private double redundancyFund;
    private double industrialTraining;
    private double nhs;

    public static PayslipHistory fromCSVLine(String line) {
        final PayslipHistory payslipHistory = new PayslipHistory();

        final List<String> parts = CSVReader.splitLine(line);
        payslipHistory.setEmployeeId(parts.get(0));
        payslipHistory.setPersonType(PersonType.fromValue(parts.get(1)));
        payslipHistory.setDate(DateUtil.dateStrToLocalDate(parts.get(2), "dd/MM/yyyy"));
        payslipHistory.setSocialInsurance(Double.parseDouble(parts.get(3)));
        payslipHistory.setCohesionFund(Double.parseDouble(parts.get(4)));
        payslipHistory.setIncomeTax(Double.parseDouble(parts.get(5)));
        payslipHistory.setRedundancyFund(Double.parseDouble(parts.get(6)));
        payslipHistory.setIndustrialTraining(Double.parseDouble(parts.get(7)));
        payslipHistory.setNhs(Double.parseDouble(parts.get(8)));

        return payslipHistory;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getSocialInsurance() {
        return socialInsurance;
    }

    public void setSocialInsurance(double socialInsurance) {
        this.socialInsurance = socialInsurance;
    }

    public double getCohesionFund() {
        return cohesionFund;
    }

    public void setCohesionFund(double cohesionFund) {
        this.cohesionFund = cohesionFund;
    }

    public double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public double getRedundancyFund() {
        return redundancyFund;
    }

    public void setRedundancyFund(double redundancyFund) {
        this.redundancyFund = redundancyFund;
    }

    public double getIndustrialTraining() {
        return industrialTraining;
    }

    public void setIndustrialTraining(double industrialTraining) {
        this.industrialTraining = industrialTraining;
    }

    public double getNhs() {
        return nhs;
    }

    public void setNhs(double nhs) {
        this.nhs = nhs;
    }

    @Override
    public String toString() {
        return "PayslipHistory{" +
                "employeeId='" + employeeId + '\'' +
                ", personType=" + personType +
                ", date=" + date +
                ", socialInsurance=" + socialInsurance +
                ", cohesionFund=" + cohesionFund +
                ", incomeTax=" + incomeTax +
                ", redundancyFund=" + redundancyFund +
                ", industrialTraining=" + industrialTraining +
                ", nhs=" + nhs +
                '}';
    }
}
