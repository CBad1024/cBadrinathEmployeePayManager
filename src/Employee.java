import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Employee implements Serializable {


    private final String name;
    private final String empId;
    private final String dob;
    private String address;
    protected TimeCard timeCard;
    double moneyDue = 0;
    private boolean isSalaried;
    private double payoutAmt;
    private int daysOffRemaining;

    private static final double dailySalaryRate = 100.0;


    private Employee(String name, String empId, String dob, String address, boolean isSalaried, double payoutAmt) {
        this.name = name;
        this.empId = empId;
        this.dob = dob;
        this.address = address;
        this.timeCard = new TimeCard();
        this.payoutAmt = payoutAmt;
        this.isSalaried = isSalaried;
        this.daysOffRemaining = 20;
    }

    public static Employee salariedEmployee(String name,String empId, String dob, String address, double payoutAmt){
        return new Employee(name , empId, dob, address, true, payoutAmt);
    }

    public static Employee wageEmployee(String name,String empId, String dob, String address, double payoutAmt){
        return new Employee(name , empId, dob, address, false, payoutAmt);
    }

    public void clockIn(LocalDateTime dateTime){
        this.timeCard.clockIn(dateTime);
    }

    public void clockOut(LocalDateTime dateTime){
        this.timeCard.clockOut(dateTime);
    }
    public double calculatePay(){ //Calculates how much money must be paid to given wage employee
        if (this.isSalaried){
            return 0; //FIXME fix salary rate
//            return dailySalaryRate; //FIXME fix salary rate
        }
        else{long overtimeDue = this.timeCard.getOvertimeHours();
            long totalNonOvertime = this.timeCard.getTotalHours() - overtimeDue;

            return Math.round((totalNonOvertime  + overtimeDue * 1.5) * this.payoutAmt * 100)/100;}


    }

    @Override
    public int hashCode() {
        return Objects.hash(empId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        return (this.empId == ((Employee)obj).empId);
    }

    public String toString() {
        StringBuilder output = new StringBuilder()
                .append("Name: " + this.name + "\n")
                .append("EmpID: " + this.empId + "\n")
                .append("DOB: " + this.dob + "\n")
                .append("Current Worklog: \n" + this.timeCard.printCurrentTimestamps() + "\n")
                .append("Worklog History: \n" + this.timeCard.printTimestampHistory() + "\n");
        return output.toString();
    }

    public String payoutReport(){
        //TODO this needs to be completed.
        StringBuilder output = new StringBuilder()
                .append("Name: " + this.name + "\n")
                .append("EmpId : " + this.empId + "\n");
        return output.toString();
    }




}


