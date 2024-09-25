package main;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static java.time.LocalDate.parse;

class EmployeeTest {

    @org.junit.jupiter.api.Test
    void calculatePayForSalariedEmployee() {
        Employee salEmp = Employee.salariedEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 100);
        Employee wageEmp = Employee.wageEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 10);
        assertEquals(0, salEmp.calculatePay());

    }

    @org.junit.jupiter.api.Test
    void calculatePayForWageEmployeeWhenNoClockInOrClockOut() {
        Employee wageEmp = Employee.wageEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 10);
        assertEquals(0, wageEmp.calculatePay());

    }

    @org.junit.jupiter.api.Test
    void calculatePayForWageEmployee() {
        Employee wageEmp = Employee.wageEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 10);
        wageEmp.clockIn(LocalDateTime.now());
        long hours = 8 ;
        int expectedWage = (int) hours * 10 ;
        wageEmp.clockOut(LocalDateTime.now().plusHours(hours));
        System.out.println(wageEmp.timeCard.getTotalHours());
        assertEquals(expectedWage, wageEmp.calculatePay());

    }

    @org.junit.jupiter.api.Test
    void testPrint(){
        Employee salEmp = Employee.salariedEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 100);
        salEmp.clockIn(LocalDateTime.now());
        salEmp.clockOut(LocalDateTime.now().plusHours(8));
        salEmp.timeCard.moveRecordsToHistory();
        salEmp.clockIn(LocalDateTime.now().plusDays(12));
        salEmp.clockOut(LocalDateTime.now().plusDays(12).plusHours(8));
        System.out.println(salEmp);
    }
}