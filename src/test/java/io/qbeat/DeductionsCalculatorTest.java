package io.qbeat;

import io.qbeat.models.Employee;
import io.qbeat.models.PersonType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DeductionsCalculatorTest {

    LocalDate startOfYear = LocalDate.of(2021, 01, 01);
    LocalDate startOfMonth = LocalDate.of(2021, 01, 02);
    LocalDate midOfMonth = LocalDate.of(2021, 01, 15);;
    private Employee payedZeroSalaryPerMonth = new Employee("1", "1111", "Homer Simpson", BigDecimal.ZERO, startOfYear);
    private Employee payedOneEuroSalaryPerMonth = new Employee("2", "1111", "Homer Simpson", BigDecimal.ONE, startOfYear);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    public void should_calculate_employee_percentages_correctly(){
        //new DeductionsCalculator(PersonType.EMPLOYEE, payedOneEuroSalaryPerMonth);

    }
}