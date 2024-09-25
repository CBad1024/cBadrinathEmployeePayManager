package main;

import java.io.Serializable;
import java.sql.SQLOutput;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.*;
import java.util.*;
import java.time.*;
import java.util.stream.Collectors;
import static java.time.DayOfWeek.*;
//TOTAL PAY: EVERYTHING THAT THEY'VE EVER BEEN PAID
public class TimeCard implements Serializable {
//    private List<Double> hoursWorked;
    private Map<LocalDate, Long> loggedMinutes;

    public void setLoggedMinutes(Map<LocalDate, Long> loggedMinutes) {
        this.loggedMinutes = loggedMinutes;
    }

    private Map<LocalDate, Long> loggedMinutesHistory;
    private LocalTime clockInTime ; //"HH:MM"

    private List<String> timestamps; //ex. ["12/1/24;9:00-17:00", "10:00-13:00"]
    private List<String> timestampsHistory; //ex. ["12/1/24;9:00-17:00", "10:00-13:00"]
    public static LocalDate currentDate ;
    public boolean isClockedIn() {
        return clockedIn;
    }

    private boolean clockedIn;

    public static List<LocalDate> holidays = Utils.loadHolidaysInPlace();

    public TimeCard(){
        this.loggedMinutes = new HashMap<LocalDate, Long>();
        this.timestamps = new ArrayList<String>();
        this.clockInTime = LocalTime.MIN;
        this.timestampsHistory = new ArrayList<>();
        this.loggedMinutesHistory = new HashMap<>();



    }

    public void clockIn(LocalTime time){
        if (!this.clockedIn) {
            System.out.println("Clocking In ... ");
            this.clockInTime = time;
            this.clockedIn = true;
        }
//        System.out.println("You are already clocked in. Please clock out before clocking in again.");
    }

    public LocalDate getDay(){
        LocalDate biggest = LocalDate.MIN;
        for (LocalDate dt: this.loggedMinutes.keySet()) {
            biggest = dt.compareTo(biggest) > 0 ? dt : biggest;

        }
        return biggest;
    }

    public void clockOut(LocalTime clockOutTime){
        if (clockedIn) {
            long minutesWorked = MINUTES.between(clockInTime, clockOutTime);
            if (minutesWorked > 0) {
                long minutesTotal = this.loggedMinutes.get(TimeCard.currentDate) == null ? minutesWorked : this.loggedMinutes.get(TimeCard.currentDate) + minutesWorked;
                this.loggedMinutes.put(TimeCard.currentDate, minutesTotal);
                LocalDateTime clockInDateTime = LocalDateTime.of(TimeCard.currentDate, clockInTime);
                LocalDateTime clockOutDateTime = LocalDateTime.of(TimeCard.currentDate, clockOutTime);
                this.timestamps.add(clockInDateTime + "     -     " + clockOutDateTime);
                this.clockedIn = false;
            }else {
                System.out.println("Clock out time should be greater than clock in time ..");
            }
        }else {
            System.out.println("Please clock in before clocking out");
        }
    }

    public double getTotalHours(){
        return this.loggedMinutes.values().stream().collect(Collectors.summingLong(Long::longValue))/60;
    }

    public int weeksWorked(){
        return (int) Math.ceil(this.loggedMinutes.values().stream().filter(e -> e > 0).count() / 5.0);
    }

    public boolean isHoliday(LocalDate dt){
        for (LocalDate date:
             this.holidays) {
            if (dt.equals(date)){
                return true;
            }
        }
        return false;
    }

    public double getOvertimeHours(){
        //Calculate for 8+ hrs/day
        // TODO - need to check if partial hours can be rounded - Check with Mr Crute
        Map<LocalDate, Double> hoursWorked = new HashMap<LocalDate, Double>();
        this.loggedMinutes.forEach((dt, min) ->{
            hoursWorked.put(dt, min/60.0);
        });
        Map<LocalDate, Double> overtimeHoursPerDay = new HashMap<>();
        hoursWorked.forEach((dt, hr) -> {
            overtimeHoursPerDay.put(dt, hr - 8.0);
        });



        //Calculate for 40+ hrs/7 days
       double totalHoursWorked = hoursWorked.values().stream().collect(Collectors.summingDouble(Double::doubleValue));

//       long weeksWorked = this.loggedMinutes.size()/7;
       long weeksWorked = this.loggedMinutes.values().stream().filter(e -> e > 0).count() / 5;
        System.out.println("Weeks Worked "+ weeksWorked);

        //Total Overtime Hours = (TotalHoursPerWeek - 40*Weeks Worked) - TotalDailyOvertime
        double totalDailyOvertime = overtimeHoursPerDay.values().stream().filter(e -> e > 0).collect(Collectors.summingDouble(Double::doubleValue));
        //fixme redo overtime


        double totalOvertimeHours = totalHoursWorked > 40 ? (totalHoursWorked - 40*weeksWorked) : totalDailyOvertime ;



        return totalOvertimeHours;

    }
    public void moveRecordsToHistory(){
        this.loggedMinutesHistory.putAll(loggedMinutes);
        this.loggedMinutes = new HashMap<>();
        this.timestampsHistory.addAll(timestamps);
        this.timestamps = new ArrayList<>();
    }

    public List<String> getTimestamps() {
        return timestamps;
    }

    public String printCurrentTimestamps() {
        String s = this.timestamps.stream().map(e -> e + "\n").collect(Collectors.joining());
        return s;
    }

    public String printTimestampHistory() {
        String s = this.timestampsHistory.stream().map(e -> e + "\n").collect(Collectors.joining());
        return s;
    }

    public double getNonOvertimeHours() {
        return getTotalHours() >= 40 ? 40 : getTotalHours();
    }

    public Map<LocalDate, Long> getLoggedMinutes() {
        return loggedMinutes;
    }
}
