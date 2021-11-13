package io.qbeat;

import io.qbeat.models.Employee;
import io.qbeat.models.PersonType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DeductionsCalculatorTest {

    private final LocalDate startOfYear = LocalDate.of(2021, 01, 01);
    private final LocalDate startOfMonth = LocalDate.of(2021, 01, 02);
    private final LocalDate midOfMonth = LocalDate.of(2021, 01, 15);
    private final Employee payedZeroSalaryPerMonth = new Employee("1", "1111", "Homer Simpson", BigDecimal.ZERO, startOfYear);
    private final Employee payedOneEuroSalaryPerMonth = new Employee("2", "1111", "Homer Simpson", BigDecimal.ONE, startOfYear);

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