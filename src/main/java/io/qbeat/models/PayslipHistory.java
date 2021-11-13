package io.qbeat.models;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.utils.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PayslipHistory {
    @Getter @Setter
    private String employeeId;
    @Getter @Setter
    private PersonType personType;
    @Getter @Setter
    private LocalDate date;
    @Getter @Setter
    private BigDecimal socialInsurance;
    @Getter @Setter
    private BigDecimal cohesionFund;
    @Getter @Setter
    private BigDecimal incomeTax;
    @Getter @Setter
    private BigDecimal redundancyFund;
    @Getter @Setter
    private BigDecimal industrialTraining;
    @Getter @Setter
    private BigDecimal nhs;

    /**
     * Gets a CSV line comma separated and returns a PayslipHistory object.
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
