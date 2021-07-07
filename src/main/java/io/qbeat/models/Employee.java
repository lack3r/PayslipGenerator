package io.qbeat.models;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.utils.DateUtil;

import java.time.LocalDate;
import java.util.List;

public class Employee {
    private final String id;
    private final String socialInsuranceId;
    private final String fullName;
    private final double grossSalary;
    private final LocalDate dateStarted;

    public Employee(String id, String socialInsuranceId, String fullName, double grossSalary, LocalDate dateStarted) {
        this.id = id;
        this.socialInsuranceId = socialInsuranceId;
        this.fullName = fullName;
        this.grossSalary = grossSalary;
        this.dateStarted = dateStarted;
    }

    /**
     * @param line A csv line
     * @return An Employee object
     */
    public static Employee fromCSVLine(String line) {
        final List<String> parts = CSVReader.splitLine(line);

        return new Employee(
                parts.get(0),
                parts.get(1),
                parts.get(2),
                Double.parseDouble(parts.get(3)),
                DateUtil.dateStrToLocalDate(parts.get(4), "dd/MM/yyyy")
        );
    }

    public String getId() {
        return id;
    }

    public String getSocialInsuranceId() {
        return socialInsuranceId;
    }

    public String getFullName() {
        return fullName;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public LocalDate getDateStarted() {
        return dateStarted;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", socialInsuranceId='" + socialInsuranceId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", grossSalary=" + grossSalary +
                ", dateStarted=" + dateStarted +
                '}';
    }
}
