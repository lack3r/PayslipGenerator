package io.qbeat.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.utils.DateUtil;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Employee implements Serializable {

    private static final long serialVersionUID = 9045863543269746292L;

    @JsonProperty("id")
    private final String id;
    @JsonProperty("socialInsuranceId")
    private final String socialInsuranceId;
    @JsonProperty("fullName")
    private final String fullName;
    @JsonProperty("grossSalary")
    private final BigDecimal grossSalary;
    @JsonProperty("dateStarted")
    private final LocalDate dateStarted;

    @JsonCreator
    public Employee(@JsonProperty("id") String id, @JsonProperty("socialInsuranceId") String socialInsuranceId, @JsonProperty("fullName") String fullName, @JsonProperty("grossSalary") BigDecimal grossSalary, @JsonProperty("dateStarted") LocalDate dateStarted) {
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
    static Employee fromCSVLine(String line) {
        final List<String> parts = CSVReader.splitLine(line);

        return new Employee(
                parts.get(0),
                parts.get(1),
                parts.get(2),
                new BigDecimal(parts.get(3)),
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

    public BigDecimal getGrossSalary() {
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
