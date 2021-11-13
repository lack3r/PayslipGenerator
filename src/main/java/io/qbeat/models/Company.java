package io.qbeat.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.qbeat.file.readers.CSVReader;
import io.qbeat.file.readers.FileReader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toCollection;

@Component
public class Company implements Serializable {

    private static final Logger logger = LogManager.getLogger(Company.class);

    @JsonProperty("name")
    private final String name;
    @JsonProperty("address")
    private final String address;
    @JsonProperty("phone")
    private final String phone;
    @JsonProperty("employees")
    private final ArrayList<Employee> employees;

    @JsonCreator
    public Company(@JsonProperty("name") String name, @JsonProperty("address") String address, @JsonProperty("phone") String phone, @JsonProperty("employees") List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.employees = new ArrayList<>(employees);
    }

    public static Company loadFromCSVFile(FileReader fileReader, String filename) throws IOException {

        List<String> fileLines;
        try {
            fileLines = fileReader.read(filename);
        } catch (IOException e) {
            throw new IOException("The Companies' input file could not be found", e);
        }

        List<String> companyInfo = CSVReader.splitLine(fileLines.remove(0));

        ArrayList<Employee> companyEmployees = fileLines.stream()
                .map(String::trim)
                .filter(line -> !line.equals(""))
                .map(Employee::fromCSVLine)
                .collect(toCollection(ArrayList::new));

        return new Company(
                companyInfo.get(0),
                companyInfo.get(1),
                companyInfo.get(2),
                companyEmployees
        );
    }

    public void exportToJSON(String filename, ObjectMapper mapper) {
        try {
            mapper.writeValue(new File(filename), this);
        } catch (IOException e) {
            logger.error("Could not export company to JSON ", e);
        }
    }

    public static Company importFromJSON(String filename, ObjectMapper mapper) throws IOException {
        try {
            Company company = mapper.readValue(new File(filename), Company.class);
            logger.info("Successfully read a {} object", Company.class);
            return company;
        } catch (IOException e) {
            logger.error("Could not import company from JSON ", e);
            throw e;
        }
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
