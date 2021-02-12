package com.qbeat.tools.models;

import com.qbeat.tools.models.Employee;

import java.util.Collections;
import java.util.List;

public class CompanyInfo {
    private final String name;
    private final String address;
    private final String phone;
    private final List<Employee> employees;

    public CompanyInfo(String name, String address, String phone, List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public List<Employee> getEmployees() {
        return Collections.unmodifiableList(employees);
    }

    @Override
    public String toString() {
        return "CompanyInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", employees=" + employees +
                '}';
    }
}
