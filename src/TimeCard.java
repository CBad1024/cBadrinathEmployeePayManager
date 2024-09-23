import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.*;
import java.util.*;
import java.time.*;
import java.util.stream.Collectors;
import static java.time.DayOfWeek.*;
//TOTAL PAY: EVERYTHING THAT THEY'VE EVER BEEN PAID
public class TimeCard implements Serializable {
//    private List<Double> hoursWorked;
    private Map<String, Long> loggedMinutes;

    public void setLoggedMinutes(Map<String, Long> loggedMinutes) {
        this.loggedMinutes = loggedMinutes;
    }

    private Map<String, Long> loggedMinutesHistory;
    private LocalDateTime clockInTime ; //"HH:MM"
//    private int dayInCycle;
    private List<String> timestamps; //ex. ["12/1/24;9:00-17:00", "10:00-13:00"]
    private List<String> timestampsHistory; //ex. ["12/1/24;9:00-17:00", "10:00-13:00"]
    static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("MM-d-yy H:mm");
    static final DateTimeFormatter dFormat = DateTimeFormatter.ofPattern("MM-d-yy");
    private int dayInPeriod;
    private boolean clockedIn;

    private List<LocalDate> holidays;



    public TimeCard(){
        this.loggedMinutes = new HashMap<String, Long>();
        this.timestamps = new ArrayList<String>();
//        this.dayInCycle = 0;
        this.clockInTime = LocalDateTime.MIN;
        this.timestampsHistory = new ArrayList<>();
        this.loggedMinutesHistory = new HashMap<>();



    }
    private String getDate(LocalDateTime dt){
        return dt.toString().split("T")[0].strip();
    }

    public void clockIn(LocalDateTime dateTime){
        if (!this.clockedIn) {
            if (!dateTime.getDayOfWeek().equals(clockInTime.getDayOfWeek())) {
                dayInPeriod++;
            }

            this.clockInTime = dateTime;
            this.clockedIn = true;
            return;

            //TODO catch if clock in is done more than once before clocking out
        }
        System.out.println("You are already clocked in. Please clock out before clocking in again.");
    }

    public void clockOut(LocalDateTime clockOutTime){
        long minutesWorked = MINUTES.between(clockInTime, clockOutTime);
        String dt = this.getDate(clockInTime);
        long minutesTotal = this.loggedMinutes.get(dt) == null ? minutesWorked :  this.loggedMinutes.get(dt) + minutesWorked;


        this.loggedMinutes.put(this.getDate(clockInTime), minutesTotal);

        this.timestamps.add(clockInTime + "     -     " + clockOutTime);

    }

    public double getTotalHours(){
        return this.loggedMinutes.values().stream().collect(Collectors.summingLong(Long::longValue))/60;
    }

    public double getBaseHours(){
        return getTotalHours() - getOvertimeHours();
    }

    public int weeksWorked(){
        return (int) this.loggedMinutes.values().stream().filter(e -> e > 0).count() / 5;
    }


    public void deletePreviousRecord(int index){
        this.timestamps.remove(index);
    }

    public void addNewRecord(int index, String record){
        this.timestamps.add(index, record);
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
        Map<String, Double> hoursWorked = new HashMap<String, Double>();
        this.loggedMinutes.forEach((dt, min) ->{
            hoursWorked.put(dt, min/60.0);
        });
        Map<String, Double> overtimeHoursPerDay = new HashMap<>();
        hoursWorked.forEach((dt, hr) -> {
            overtimeHoursPerDay.put(dt, hr - 8.0);
        });

//        System.out.println(overtimeHoursPerDay);

        //Calculate for 40+ hrs/7 days
       double totalHoursWorked = hoursWorked.values().stream().collect(Collectors.summingDouble(Double::doubleValue));

//       long weeksWorked = this.loggedMinutes.size()/7;
       long weeksWorked = this.loggedMinutes.values().stream().filter(e -> e > 0).count() / 5;
        System.out.println("Weeks Worked "+ weeksWorked);

        //Total Overtime Hours = (TotalHoursPerWeek - 40*Weeks Worked) - TotalDailyOvertime
        double totalDailyOvertime = overtimeHoursPerDay.values().stream().filter(e -> e > 0).collect(Collectors.summingDouble(Double::doubleValue));
        //fixme redo overtime


//        double totalOvertimeHours = totalHoursWorked > 40 ? (totalHoursWorked - 40*weeksWorked) : totalDailyOvertime ;
        double totalOvertimeHours = totalDailyOvertime + totalHoursWorked - 40.0/7*weeksWorked;



        return totalOvertimeHours;

    }
    public void moveRecordsToHistory(){
        this.loggedMinutesHistory.putAll(loggedMinutes);
        this.loggedMinutes = new HashMap<>();
        this.timestampsHistory.addAll(timestamps);
        this.timestamps = new ArrayList<>();
    }

    public boolean isWeekend(LocalDateTime dt){
        return dt.getDayOfWeek().equals(SATURDAY) || dt.getDayOfWeek().equals(SUNDAY);
    }


    public String printCurrentTimestamps() {
        String s = this.timestamps.stream().map(e -> e + "\n").collect(Collectors.joining());
        return s;
    }

    public String printTimestampHistory() {
        String s = this.timestampsHistory.stream().map(e -> e + "\n").collect(Collectors.joining());
        return s;
    }

}
