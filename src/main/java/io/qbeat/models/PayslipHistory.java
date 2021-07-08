package io.qbeat.models;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.utils.DateUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PayslipHistory {
    private String employeeId;
    private PersonType personType;
    private LocalDate date;
    private BigDecimal socialInsurance;
    private BigDecimal cohesionFund;
    private BigDecimal incomeTax;
    private BigDecimal redundancyFund;
    private BigDecimal industrialTraining;
    private BigDecimal nhs;

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
        payslipHistory.setSocialInsurance(new BigDecimal(parts.get(3)));
        payslipHistory.setCohesionFund(new BigDecimal(parts.get(4)));
        payslipHistory.setIncomeTax(new BigDecimal(parts.get(5)));
        payslipHistory.setRedundancyFund(new BigDecimal(parts.get(6)));
        payslipHistory.setIndustrialTraining(new BigDecimal(parts.get(7)));
        payslipHistory.setNhs(new BigDecimal(parts.get(8)));

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

    public BigDecimal getSocialInsurance() {
        return socialInsurance;
    }

    public void setSocialInsurance(BigDecimal socialInsurance) {
        this.socialInsurance = socialInsurance;
    }

    public BigDecimal getCohesionFund() {
        return cohesionFund;
    }

    public void setCohesionFund(BigDecimal cohesionFund) {
        this.cohesionFund = cohesionFund;
    }

    public BigDecimal getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(BigDecimal incomeTax) {
        this.incomeTax = incomeTax;
    }

    public BigDecimal getRedundancyFund() {
        return redundancyFund;
    }

    public void setRedundancyFund(BigDecimal redundancyFund) {
        this.redundancyFund = redundancyFund;
    }

    public BigDecimal getIndustrialTraining() {
        return industrialTraining;
    }

    public void setIndustrialTraining(BigDecimal industrialTraining) {
        this.industrialTraining = industrialTraining;
    }

    public BigDecimal getNhs() {
        return nhs;
    }

    public void setNhs(BigDecimal nhs) {
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
