package com.qbeat.tools.models;

import com.qbeat.tools.config.CSVReader;
import com.qbeat.tools.config.PersonType;
import com.qbeat.tools.utils.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /**
     * Gets a CSV line comma separated and returns a PayslipHistory object
     *
     * @param line A CSV line comma separated
     * @return A PayslipHistory object
     */
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

    /**
     * Gets a PayslipHistory object and returns a CSV line comma separated
     *
     * @param payslipHistory A PayslipHistory object
     * @return A CSV line comma separated
     */
    public static String toCSVLine(PayslipHistory payslipHistory) {
        List<String> data = new ArrayList<>();

        data.add(payslipHistory.getEmployeeId());
        data.add(payslipHistory.getPersonType().getValue());
        data.add(DateUtil.localDateToDateStr(payslipHistory.getDate(), "dd/MM/yyyy"));
        data.add(String.valueOf(payslipHistory.getSocialInsurance()));
        data.add(String.valueOf(payslipHistory.getCohesionFund()));
        data.add(String.valueOf(payslipHistory.getIncomeTax()));
        data.add(String.valueOf(payslipHistory.getRedundancyFund()));
        data.add(String.valueOf(payslipHistory.getIndustrialTraining()));
        data.add(String.valueOf(payslipHistory.getNhs()));

        return data.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayslipHistory)) return false;
        PayslipHistory that = (PayslipHistory) o;
        return Objects.equals(getEmployeeId(), that.getEmployeeId()) && getPersonType() == that.getPersonType() && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId(), getPersonType(), getDate());
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
