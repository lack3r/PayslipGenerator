package io.qbeat;

import io.qbeat.models.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;

class DeductionsCalculatorTest {

    private final LocalDate startOfYear = LocalDate.of(2021, 1, 1);
    private final LocalDate startOfMonth = LocalDate.of(2021, 1, 2);
    private final LocalDate midOfMonth = LocalDate.of(2021, 1, 15);
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