package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.lang.*;
import java.io.*;


public class Main {
    static String savefile = "EmployeeRecords.ser";
    static String payReportFile = "PAYREPORT.payrpt";
    static Scanner in = new Scanner(System.in);
    static Set<Employee> empSet = loadOnStart();
    static DateTimeFormatter timeFormatter =  DateTimeFormatter.ofPattern("hh:mm");

    static LocalDate startDate = Utils.startDate;
    static boolean isDayStarted = (startDate != null);

    public static void main(String[] args) {

        empSet.forEach(System.out::println);
        try {

            // Get User Input
            while (true) {
                try {
                    int input = getTopLevelInput();

                    switch (input) {
                        case 1 -> {
                            Set<Employee> empSetNew = provideEmployeeDetails();
                            empSetNew.addAll(empSet);
                            empSet = new HashSet<>(empSetNew);
                        }
                        case 2 -> {
                            generatePayReport(); //TODO
                        }
                        case 3 -> {
                            //View employee past records
                            loadPastRecords();

                        }
                        case 4 -> {
                            //Emp signin
                            empLogin();
                        }
                        case 5 -> {
                            //next day
                            startDay();
                        }
                        case 6 -> {
                            //next day
                            endDay();
                        }
                        case 7 -> {
                            //holiday
                            addHoliday();
                        }
                        case 8 -> {
                            //Exit
                            saveOnExit(empSet);
                        }


                    }


                } catch (InvalidInputException e) {

                }


            }
            //Get input

            //ask to 1: Provide Emp details

//            Scanner in = new Scanner(System.in);
//
//            System.out.println("Hello! Welcome to Badrinath Employee Management System (BEMS). What would you like to do?");


            //Store employee records


            //Timekeeping


            //Pay Calculation


            //Error Handling


            //Report


            //
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                saveOnExit(empSet);
            }
            System.exit(0);


        }

    }

    private static void startDay() {
        if (isDayStarted) {
            System.out.println("You have already started the day. ");
            return;
        }
        if (startDate != null) {
            LocalDate nextDay = getNextWorkingDay(startDate);
            String input = getInput("Starting Day [ " + startDate.format(DateTimeFormatter.ISO_DATE) + " ] : Press Any Key ..");
            isDayStarted = true ;
            Utils.startDate = nextDay;
            startDate = nextDay ;
        } else {
            while(true){
                LocalDate date = checkValidDate( " Start Day [YYYY-MM-dd] : " );
                if(TimeCard.holidays.contains(date)){
                    System.out.println("That day is a holiday. Please enter a non-holiday...");
                }
                else{
                    startDate = date;
                    Utils.startDate = startDate ;
                    isDayStarted = true;
                    break;
                }
            }

        }
    }

    private static LocalDate getNextWorkingDay(LocalDate dt) {
        while(true){
            LocalDate nextDay = dt.plusDays(1);
            if(TimeCard.holidays.contains(nextDay)){
                dt = nextDay;
            }
            else{
                return nextDay;
            }
        }
    }
    private static void endDay() {
        if (!isDayStarted) {
            System.out.println("Day has already ended. Please start the day first ..");
            return;
        }
        String input = getInput("Would you like to like to end the day [y/n]? Today is " + startDate);
        switch(input.toUpperCase()){
            case "Y":
                empSet.forEach(e -> {
                    e.incrementDay();
                });
            default:
                System.out.println("OK! Returning to homescreen...");
        }
    }

    private static void addHoliday() {
        String input = getInput("This application comes with an inbuilt holiday calendar. Would you like to add to this calendar or use it as is? [y/n]");

        Set<LocalDate> dateSet = new HashSet<>();
        dateSet.addAll(TimeCard.holidays);

        if(input.equalsIgnoreCase("Y")){
            while(true){
                LocalDate date = checkValidDate("Enter a date (YYYY-MM-dd). If you want to exit, enter 2000-01-01");
                if (date.equals(LocalDate.parse("2000-01-01"))) {
                    break;
                } else {
                    dateSet.add(date);
                }
            }
        }
        TimeCard.holidays = dateSet.stream().toList();
    }

    private static String getInput(String prompt) {
        System.out.println(prompt);
        return in.next().strip();
    }

    private static int getIntInput(String prompt) {
        while (true){
            try{
                System.out.println(prompt);
                return in.nextInt();
            } catch ( NoSuchElementException | IllegalStateException e){
                System.out.println("Please enter valid number .. ");
            }
        }
    }

    private static void empLogin() {
        if (!isDayStarted) {
            System.out.println("Before checking in as an employee, Start the day ..");
            return ;
        }
        String ID = getInput("Please enter your employee ID");
        Employee emp = empSet.stream().filter(e -> e.getEmpId().equalsIgnoreCase(ID)).findFirst().orElse(null);
        if(emp == null){
            System.out.println("No such employee exists");
        } else{
            empInterface(emp);
        }
    }

    private static void empInterface(Employee e) {
        System.out.println("Logged in as " + e.getName());
        while (true) {
            int input =  getIntInput("What would you like to do?\n[1] Clock In\n[2] Clock Out\n[3] Return to start");
            switch (input) {
                case 1:
                    if (!e.timeCard.isClockedIn()) {
                        LocalDate date = checkValidDate("Please enter the date (YYYY-MM-dd)");
                        LocalTime time = checkValidTime("Please enter time (HH:mm) in 24 hour format:");
                        e.clockIn(LocalDateTime.of(date, time));
                    } else {
                        System.out.println("Already clocked in. Please clock out ...");
                    }
                    break ;
                case 2:
                    if (e.timeCard.isClockedIn()) {
                        LocalTime t = checkValidTime("Please enter clock out (HH:mm) in 24 hour format:");
                    }else {
                        System.out.println("Please clock in first before clocking out .. ");
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private static void loadPastRecords() { //Loads previous pay report records.
        //fixme


    }

    private static void generatePayReport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(startDate + "_" +  payReportFile))) {
            String header = "PAYOUT REPORT\n" +
                    "____________________________________________________\n\n" +
                    "Start of Period: " + startDate + "\t\tDay of Filing" + TimeCard.currentDate + "\n" +
                    "Total Days in Period" + startDate.until(TimeCard.currentDate) + "\n\n\n" +
                    "EMPLOYEE PAYOUT\n" +
                    "____________________________________________________\n\n" ;
            writer.write(header);
            empSet.forEach( e ->
            {
                try {
                    writer.write(e.payReport());
                } catch (IOException ex) {
                    System.out.println("Error while writing Payout Report ..");
                }
            });

        } catch (IOException ioe){

        }

        // FIXME
    }



    public static Set<Employee> provideEmployeeDetails() {
        Set<Employee> empList = new HashSet<>();
        while (true) {
            String name = getInput("Please list the employee data...\nName (Last,First NO SPACE): ");

            String empId = getInput("EmpID: ");

            LocalDate dob = checkValidDate("DOB (YYYY-MM-dd): ");

            String address = getInput("Address: ");

            boolean isSalaried = checkSalaried();

            double payoutAmt = getPayout();

            Employee e = isSalaried ?
                    Employee.salariedEmployee(name, empId, dob, address, payoutAmt)
                    : Employee.wageEmployee(name, empId, dob, address, payoutAmt);
            empList.add(e);

            System.out.println("Continue adding next employee [y/n] : ");
            String addInput = in.next().strip();
            if (!addInput.equalsIgnoreCase("Y")) {
                break;
            }
        }
        return empList;
    }


    private static double getPayout() {
        while (true) {
            System.out.println("Payout amount(daily wage/yearly salary): ");
            try {
                double rate = in.nextDouble();
                return rate;

            } catch (Exception e) {
                System.out.println("Please enter a valid rate (just the number)");
            }
        }
    }

    private static boolean checkSalaried() {

        while (true) {
            System.out.println("Salaried/Wage? [s/w]: ");
            String input = in.next().strip();
            switch (input.toUpperCase()) {
                case "S":
                    return true;
                case "W":
                    return false;
                default:
                    System.out.println("Please enter either \"s\" or \"w\" for a salaried or wage employee");
            }
        }
    }

    private static LocalDate checkValidDate(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                String input = in.next().strip();
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid Date Format, cannot parse. Please try again as per the format...");
            }
        }
    }

    private static LocalTime checkValidTime(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                LocalTime input = LocalTime.parse(in.next().strip());
                return LocalTime.parse(input.format(DateTimeFormatter.ISO_LOCAL_TIME));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid Time Format, cannot parse. Please try again as per the format...");
            }
        }
    }


    private static int getTopLevelInput() throws InvalidInputException {
        System.out.println("Welcome to the Badrinath Employee Database (BED). What would you like to do?");
        System.out.println("[1] Provide employee details\n[2] Generate Pay Report\n[3] View employee past records\n[4] Employee Sign-In\n[5] Start Day\n[6] End Day\n[7] Add Holiday\n[8] Exit");
        int input = in.nextInt();
        switch (input) {
            case 1, 2, 3, 4, 5, 6, 7, 8:
                return input;
            default:
                System.out.println("Please choose from the valid options");
                throw new InvalidInputException();
        }
    }

    public void runPayReport() {
        //run pay report & store in pay report file
        //Move temporary record info to permanent record file
    }

    public static void saveOnExit(Set<Employee> empSet) {
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile));
                ObjectOutputStream oosUtils = new ObjectOutputStream(new FileOutputStream("Utils-"+ savefile));
        ) {
            oos.writeObject(empSet);
            Utils u = new Utils();
            oosUtils.writeObject(u);
            System.out.println("I am being called and I wrote data");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file to serialize ... Exiting without saving state ..");
        } catch (IOException e) {
            System.out.println("Cannot serialize ... Exiting without saving state ..");
        }
        finally{
            System.exit(0);
        }
    }


    public static Set<Employee> loadOnStart() {
        Set<Employee> employees = new HashSet<>();
        // Restore state of the program from a saved file which is serialized
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
                ObjectInputStream oisUtils = new ObjectInputStream(new FileInputStream("Utils-"+ savefile));
        ) {
            employees = (Set<Employee>) ois.readObject();
            Utils u = (Utils) oisUtils.readObject();
            Main.startDate = Utils.startDate ;
        } catch (FileNotFoundException fe) {
            System.out.println("Restore file not found .. Continuing ..");
        } catch (IOException e) {
            System.out.println("Restore file not deserializable .. Continuing ..");
        } catch (ClassNotFoundException e) {
            System.out.println("Restoration not possible .. Continuing ..");
        } finally {
            return employees;
        }
    }
}
