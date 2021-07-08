package io.qbeat.models;

import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Company {
    private final String name;
    private final String address;
    private final String phone;
    private final List<Employee> employees;

    public Company(String name, String address, String phone, List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.employees = employees;
    }

    public static Company loadFromCSVFile(FileReader fileReader, String filename) {
        List<String> fileLines = fileReader.read(filename);
        List<String> companyInfo = CSVReader.splitLine(fileLines.remove(0));

        List<Employee> companyEmployees = fileLines.stream()
                .map(String::trim)
                .filter(line -> !line.equals(""))
                .map(Employee::fromCSVLine)
                .collect(Collectors.toList());

        return new Company(
                companyInfo.get(0),
                companyInfo.get(1),
                companyInfo.get(2),
                companyEmployees
        );
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
        return "Company{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", employees=" + employees +
                '}';
    }
}
