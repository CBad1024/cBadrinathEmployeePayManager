package main;

import java.io.Serializable;

import static java.text.MessageFormat.format;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Employee implements Serializable {
//TODO say in UI how many days in pay period so far

    private String name;
    private final String empId;
    private final LocalDate dob;
    private String address;
    TimeCard timeCard;
    double moneyDue = 0;
    double totalPay = 0;
    private boolean isSalaried;
    private double payRate; //either wage/hr or salary/year
    private int daysOffRemaining;


    private Employee(String name, String empId, LocalDate dob, String address, boolean isSalaried, double payRate) {
        this.name = name;
        this.empId = empId;
        this.dob = dob;
        this.address = address;
        this.timeCard = new TimeCard();
        this.payRate = payRate;
        this.isSalaried = isSalaried;
        this.daysOffRemaining = 20;
    }

    public String getEmpId() {
        return empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public TimeCard getTimeCard() {
        return timeCard;
    }

    public double getMoneyDue() {
        return moneyDue;
    }

    public boolean isSalaried() {
        return isSalaried;
    }

    public double getPayRate() {
        return payRate;
    }

    public int getDaysOffRemaining() {
        return daysOffRemaining;
    }

    public static Employee salariedEmployee(String name, String empId, LocalDate dob, String address, double payoutAmt) {
        return new Employee(name, empId, dob, address, true, payoutAmt);
    }

    public static Employee wageEmployee(String name, String empId, LocalDate dob, String address, double payoutAmt) {
        return new Employee(name, empId, dob, address, false, payoutAmt);
    }

    public void clockIn(LocalTime time) {
        this.timeCard.clockIn(time);
    }

    public void clockOut(LocalTime dateTime) {
        this.timeCard.clockOut(dateTime);
    }

    public double calculatePay() { //Calculates how much money must be paid to given employee

        if (this.isSalaried) { //Salaried Employee Pay logic: paid the same regardless of time worked,
            //
            double weeklySalary = this.payRate / 52.0;
            double dailySalary = weeklySalary / 5.0;
            this.timeCard.getLoggedMinutes().values().forEach(e -> {
                if (e == 0) {
                    daysOffRemaining--; //if daysOffRemaining < 0, this will amount to that many days of pay getting taken off
                }
            });

            int daysNotWorked = -1 * daysOffRemaining;

            moneyDue = daysOffRemaining > 0 ? this.timeCard.weeksWorked() * weeklySalary : this.timeCard.weeksWorked() * weeklySalary - daysNotWorked * dailySalary;

        } else { //Wage Employee pay logic: paid based on hours worked. Overtime also calculated.
            double overtimeDue = this.timeCard.getOvertimeHours();
            double totalNonOvertime = this.timeCard.getNonOvertimeHours();

            moneyDue = Math.round((totalNonOvertime + overtimeDue * 1.5) * this.payRate * 100) / 100;
        }
        return moneyDue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId);
    }

    public double allMoneyEarned() {
        return totalPay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        return (this.empId == ((Employee) obj).empId);
    }

    public String toString() {
        StringBuilder output = new StringBuilder()
                .append("Name: " + this.name + "\n")
                .append("EmpID: " + this.empId + "\n")
                .append("DOB: " + this.dob + "\n")
                .append("Last Working Date : " + TimeCard.currentDate + "\n")
                .append("Current Worklog: \n" + this.timeCard.printCurrentTimestamps() + "\n")
                .append("Worklog History: \n" + this.timeCard.printTimestampHistory() + "\n");
        return output.toString();
    }

    public String payReport() {
        //TODO this needs to be completed.
        String paymentInfo = isSalaried ? format("Salary: $ {0} per week", payRate)
                : format("Wage: $ {0} per hour", payRate);

        StringBuilder output = new StringBuilder()
                .append("Name: " + this.name + "\n")
                .append("EmpId : " + this.empId + "\n")
                .append(paymentInfo + "\n")
                .append("Pay this period: $" + moneyDue + "\n")
                .append("Total Pay (including current pay cycle): $" + (allMoneyEarned() + moneyDue) + "\n");


        return output.toString();
    }



    public LocalDate getDay() {
        return this.timeCard.getDay();
    }



    public void storeMoneyData() {
        totalPay += moneyDue;
        moneyDue = 0;
    }

    public double getWeeklyPayRate(){
        return getPayRate()/52.0;
    }


}


