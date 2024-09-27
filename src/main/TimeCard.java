package main;

import java.io.Serializable;

import static java.time.temporal.ChronoUnit.*;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.*;
import java.util.stream.Collectors;


//Time Card Class. Stores minutes logged each day as well as current day.
public class TimeCard implements Serializable {
    private Map<LocalDate, Long> loggedMinutes; //map of minutes logged each day.



    private Map<LocalDate, Long> loggedMinutesHistory;
    private LocalTime clockInTime ; //"HH:MM"
    static LocalDate startDate; //Starting date for the current pay period

    private List<String> timestamps; //ex. ["12/1/24;9:00-17:00", "10:00-13:00"]
    private List<String> timestampsHistory; //ex. ["12/1/24;9:00-17:00", "10:00-13:00"]
    public static LocalDate currentDate ; //Stores current date
    public boolean isClockedIn() {
        return clockedIn;
    }

    private boolean clockedIn; //stores if the employee is clocked in

    public static List<LocalDate> holidays = Utils.loadHolidaysInPlace();

    //initializes time card
    public TimeCard(){
        this.loggedMinutes = new HashMap<LocalDate, Long>();
        this.timestamps = new ArrayList<String>();
        this.clockInTime = LocalTime.MIN;
        this.timestampsHistory = new ArrayList<>();
        this.loggedMinutesHistory = new HashMap<>();
    }

    //Clocks in employee
    public void clockIn(LocalTime time){
            System.out.println("Clocking In ... ");
            this.clockInTime = time;
            this.clockedIn = true;
    }


    //Adds zero minutes to the logged minutes on the current day. This is makes it easier to calculate the wage for a 7 day period.
    public void addZeroMinutes(){
        this.loggedMinutes.put(currentDate, 0l);
    }

    //Returns current date.
    public LocalDate getDay(){
        return currentDate;
    }


    public void setLoggedMinutes(Map<LocalDate, Long> loggedMinutes) {
        this.loggedMinutes = loggedMinutes;
    }

    //Clocks out user. Adds hours worked to the loggedMinutes map
    public void clockOut(LocalTime clockOutTime){
        while(true){
            long minutesWorked = MINUTES.between(clockInTime, clockOutTime);
            if (minutesWorked > 0) {
                long minutesTotal = this.loggedMinutes.get(TimeCard.currentDate) == null ? minutesWorked : this.loggedMinutes.get(TimeCard.currentDate) + minutesWorked;
                this.loggedMinutes.put(TimeCard.currentDate, minutesTotal);
                LocalDateTime clockInDateTime = LocalDateTime.of(TimeCard.currentDate, clockInTime);
                LocalDateTime clockOutDateTime = LocalDateTime.of(TimeCard.currentDate, clockOutTime);
                this.timestamps.add(clockInDateTime + "     -     " + clockOutDateTime);
                this.clockedIn = false;
                break;
            } else {
                System.out.println("Clock out time should be greater than clock in time ..");
            }
        }
    }


    //Returns total hours worked.
    public double getTotalHours(){
        return this.loggedMinutes.values().stream().collect(Collectors.summingLong(Long::longValue))/60;
    }



    //Returns number of weeks worked
    public int weeksWorked(){
        return (int) Math.ceil(this.loggedMinutes.values().stream().filter(e -> e > 0).count() / 5.0);
    }


    //Returns number of overtime hours worked for wage employees. There are two kinds of overtime: daily overtime, calculated for 8+hr/day, and weekly overtime,
    // calculated for 40+hr/week.
    public double getOvertimeHours(){
        //Calculate overtime for 8+ hrs/day
        Map<LocalDate, Double> hoursWorked = new HashMap<LocalDate, Double>();
        this.loggedMinutes.forEach((dt, min) ->{
            hoursWorked.put(dt, min/60.0);
        });
        Map<LocalDate, Double> overtimeHoursPerDay = new HashMap<>();
        hoursWorked.forEach((dt, hr) -> {
            overtimeHoursPerDay.put(dt, hr - 8.0);
        });

        //Calculate overtime for 40+ hrs/7 days
       double totalHoursWorked = hoursWorked.values().stream().collect(Collectors.summingDouble(Double::doubleValue));

       long weeksWorked = (long) Math.ceil(this.loggedMinutes.size()/7.0);
;
        System.out.println("Weeks Worked "+ weeksWorked);

        //Total Overtime Hours = Greater between daily and weekly overtime hours
        double totalDailyOvertime = overtimeHoursPerDay.values().stream().filter(e -> e > 0).collect(Collectors.summingDouble(Double::doubleValue));
        double totalWeeklyOvertime = totalHoursWorked - 40.0*weeksWorked;
        double totalOvertimeHours = totalDailyOvertime > totalWeeklyOvertime ? totalDailyOvertime : totalWeeklyOvertime ;

        return totalOvertimeHours;

    }

    //Moves timestamps and logged minutes records to history objects
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

    //Returns non overtime hours (totalhours - 40 if totalHours>40, 0 otherwise).
    public double getNonOvertimeHours() {
        return getTotalHours() >= 40 ? 40 : getTotalHours();
    }



    public Map<LocalDate, Long> getLoggedMinutes() {
        return loggedMinutes;
    }

    //Adds timestamp at given date, start and end times, and index.
    public void addTimeStamp(LocalDate day, LocalTime start, LocalTime end, int index) {
        this.timestamps.add(index, LocalDateTime.of(day, start) + "     -     " + LocalDateTime.of(day, end));
        long currentMins = this.loggedMinutes.get(day);
        this.loggedMinutes.put(day, currentMins + start.until(end, MINUTES));
    }


    //Removes timestamp from given index. This also affects the number of logged minutes for that day
    public void removeTimeStamp(int index) {
        List<String> currentStamp = Arrays.asList(this.timestamps.get(index).split("-"));
        currentStamp.forEach(e -> {
            e.strip();
        });
        LocalDateTime dateTime = LocalDateTime.parse(currentStamp.get(0)); //start day and time
        LocalDate day = dateTime.toLocalDate();
        LocalTime start = dateTime.toLocalTime();
        LocalTime end = LocalDateTime.parse(currentStamp.get(1)).toLocalTime();//end datetime -> time
        long currentMins = this.loggedMinutes.get(day);
        this.loggedMinutes.put(day, currentMins - start.until(end, MINUTES));
    }
}
