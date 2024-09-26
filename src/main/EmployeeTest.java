package main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static java.time.LocalDate.parse;
import static main.Employee.*;

class EmployeeTest {

//    @org.junit.jupiter.api.Test
//    void calculatePayForSalariedEmployee() {
//        Employee salEmp = Employee.salariedEmployee("Badri", "E123", parse("1975-07-19"), "Pershing", 100);
//        Employee wageEmp = Employee.wageEmployee("Badri", "E123", parse("1975-07-19"), "Pershing", 10);
//        assertEquals(0, salEmp.calculatePay());
//
//    }

    @org.junit.jupiter.api.Test
    void calculatePayForWageEmployeeWhenNoClockInOrClockOut() {
        Employee wageEmp = Employee.wageEmployee("Badri", "E123", parse("1975-07-19"), "Pershing", 10);
        assertEquals(0, wageEmp.calculatePay());

    }

    static Employee wageEmp(String empId, double hourlyrate) {
        return wageEmployee("E", empId, LocalDate.parse("2020-01-01"), "Address", hourlyrate);
    }
    static Stream<Arguments> wageEmployeeData() {
        return Stream.of(
                Arguments.of(wageEmp("E1", 10), "09:00", "10:30", 15),
                Arguments.of(wageEmp("E2", 100), "09:00", "17:00", 800 ),
                Arguments.of(wageEmp("31", 1), "09:00", "20:00", 12.5 ),
                Arguments.of(wageEmp("E4", 1000), "09:00", "09:15", 250 )
        );
    }
    @ParameterizedTest
    @MethodSource("wageEmployeeData")
    void calculatePayForWageEmployee(Employee wageEmp, String clockIn, String clockOut, double expectedWage) {
        TimeCard.currentDate = LocalDate.parse("2024-01-02");
        wageEmp.clockIn(LocalTime.parse(clockIn));
        wageEmp.clockOut(LocalTime.parse(clockOut));
//        System.out.println(wageEmp.timeCard.getTotalHours());
        assertEquals(expectedWage, wageEmp.calculatePay());

    }



    @ParameterizedTest



    @org.junit.jupiter.api.Test
    void calculatePayForSalariedEmployee(){
        TimeCard.currentDate = LocalDate.parse("2024-01-02");
        Employee salaryEmp = Employee.salariedEmployee("Chaaranath", "1", parse("2008-03-05"), "Pershing", 52.0);
        salaryEmp.clockIn(LocalTime.now());
        salaryEmp.clockOut(LocalTime.now().plusHours(8));
        double expectedWage = salaryEmp.getWeeklyPayRate();
        System.out.println(salaryEmp.timeCard.weeksWorked());

        assertEquals(salaryEmp.calculatePay(), expectedWage);
    }

//    @org.junit.jupiter.api.Test
//    void testPrint(){
//        Employee salEmp = Employee.salariedEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 100);
//        salEmp.clockIn(LocalTime.now());
//        salEmp.clockOut(LocalDateTime.now().plusHours(8));
//        salEmp.timeCard.moveRecordsToHistory();
//        salEmp.clockIn(LocalTime.now());
//        salEmp.clockOut(LocalDateTime.now().plusDays(12).plusHours(8));
//        System.out.println(salEmp);
//    }
}