package com.qbeat.tools.models;

import java.time.LocalDate;

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
