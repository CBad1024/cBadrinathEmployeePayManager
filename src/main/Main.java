package main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.lang.*;
import java.io.*;
import static java.lang.System.out;
import static main.TimeCard.*;

//Main Class for Employee Pay Manager. Asks user input
public class Main {
    static String savefile = "EmployeeRecords.ser"; //File for writing the employee records
    static String payReportFile = "PAYREPORT.payrpt"; // Filename for the payout reports. Payout Reports stored in form DATE-PAYOUTREPORT.payrpt
    static Scanner in = new Scanner(System.in);
    static Set<Employee> empSet = loadOnStart(); //Set of employees. Employees loaded from EmployeeRecords.ser
    static boolean dayHasStarted = (startDate != null);

    public static void main(String[] args) {
        in.useDelimiter("\\n");
        try {
            while (true) { //Main program input
                    int input = getTopLevelInput(in);
                    switch (input) {
                        case 1 -> {
                            Set<Employee> empSetNew = provideEmployeeDetails(in); //Allows user to provide employee information, making a new employee object
                            empSetNew.addAll(empSet);
                            empSet = new HashSet<>(empSetNew); //add new employees to existing employee set
                        }
                        case 2 -> {
                            generatePayReport(); //Generates Pay Report
                        }
                        case 3 -> {
                            out.println("Please open the files that end in .payrpt format ..");
                        }
                        case 4 -> {
                            empLogin(in); // Allows employees to log in to the system. Employees can clock in, clock out, and edit their working hours.
                        }
                        case 5 -> {
                            startDay(in); //Starts day. Employees can only clock in after day has started.
                        }
                        case 6 -> {
                            endDay(in); //Ends day. Tells program to go to next non-holiday workday.
                        }
                        case 7 -> {
                            addHoliday(in); //Allows user to add a holiday to the database. Employees can't clock in on holidays, and the holidays get skipped in the database (ex. if 1/5 is a holiday the schedule
                            // will go 1/4 -> 1/6)
                        }
                        case 8 -> {
                            saveOnExit(empSet); //Saves employee and timecard information to .ser files unitl next reboot.
                        }
                    }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                saveOnExit(empSet); //saves if there is an escape sequence
            }
        }
    }

    //Starts day. Employees can only clock in after day has started.
    private static void startDay(Scanner in) {
        //If day has already started, cannot start day again.
        if (dayHasStarted) {
            out.println("You have already started the day. ");
            return;
        }
        //If there is already a start day, print the current day
        if (startDate != null) {
            currentDate = getNextWorkingDay(currentDate);
            getInput("Starting Day [ " + currentDate.format(DateTimeFormatter.ISO_DATE) + " ] : Press Any Key ..", in);
            dayHasStarted = true;
        } else {
            //If there is not already a start day, then ask user to input a day.
            while (true) {
                LocalDate dt = checkValidDate(" Start Day [YYYY-MM-dd] : ", in);
                //If chosen day is holiday, reject input
                if (holidays.contains(dt)) {
                    out.println("That day is a holiday. Please enter a non-holiday...");
                } else {
                    startDate = dt;
                    currentDate = dt;
                    dayHasStarted = true;
                    break;
                }
            }
        }
        empSet.forEach(e -> {
            e.timeCard.addZeroMinutes(); //Making sure that every employee is registered as having some activity each day (for wage calculation purposes)
        });
    }


    //Returns next non-holiday working day.
    private static LocalDate getNextWorkingDay(LocalDate dt) {
        while (true) {
            LocalDate nextDay = dt.plusDays(1);
            if (holidays.contains(nextDay)) {
                dt = nextDay;
            } else {
                return nextDay;
            }
        }
    }


    //Ends day. Current day moves to next working day on call.
    private static void endDay(Scanner in) {
        //If day has already ended
        if (!dayHasStarted) {
            out.println("Day has already ended. Please start the day first ..");
            return;
        }
        String input = getInput("Would you like to like to end the day [y/n]? Today is " + currentDate, in);
        switch (input.toUpperCase()) {
            case "Y":
                dayHasStarted = false;
                break;
            default:
                out.println("OK! Returning to homescreen...");
        }

    }



    //Adds holiday to list. Asks user for dates to be added to holiday list
    private static void addHoliday(Scanner in) {
        String input = getInput("This application comes with an inbuilt holiday calendar. Would you like to view the current holiday list? [y/n]", in);
        if(input.equalsIgnoreCase("N")){
            return;
        }

        holidays.forEach(System.out::println);

        String input2 = getInput("Would you like to add a holiday? [y/n]", in);
        if(input2.equalsIgnoreCase("N")){
            return;
        }

        Set<LocalDate> dateSet = new HashSet<>();
        dateSet.addAll(holidays);

        if (input.equalsIgnoreCase("Y")) {
            while (true) {
                LocalDate date = checkValidDate("Enter a date (YYYY-MM-dd). If you want to exit, enter 2000-01-01", in);
                if (date.equals(LocalDate.parse("2000-01-01"))) {
                    break;
                } else {
                    dateSet.add(date);
                }
            }
        }
        holidays = dateSet.stream().toList();
    }

    //Helper method to get string input from user.
    private static String getInput(String prompt, Scanner in) {
        out.println(prompt);
        return in.next().strip();
    }
    //Helper method to get integer input from user
    private static int getIntInput(String prompt, Scanner in) {
        while (true) {
            try {
                out.println(prompt);
                return in.nextInt();
            } catch (NoSuchElementException | IllegalStateException e) {
                out.println("Please enter valid number .. ");
            }
        }
    }

    //Helper method to get double input from user.
    private static double getDoubleInput(String prompt, Scanner in) {
        while (true) {
            try {
                out.println(prompt);
                return in.nextInt();
            } catch (NoSuchElementException | IllegalStateException e) {
                out.println("Please enter valid number .. ");
            }
        }
    }


    //Employee login. Allows employees to log into system. Logged in employees can edit records, clock in/ clock out, and view past records
    private static void empLogin(Scanner  in) {
        //Day must start before any employee action
        if (!dayHasStarted) {
            out.println("Before checking in as an employee, Start the day ..");
            return;
        }
        String ID = getInput("Please enter your employee ID", in);
        Employee emp = empSet.stream().filter(e -> e.getEmpId().equalsIgnoreCase(ID)).findFirst().orElse(null); //Finds employee with given ID
        if (emp == null) {
            out.println("No such employee exists");
        } else {
            empInterface(emp, in); // Employee User interface. Logged in employees can edit records, clock in/ clock out
        }
    }

    //Employee user interface. Logged in employees can edit records, clock in/ clock out, see past records.
    private static void empInterface(Employee e, Scanner in) {
        out.println("Logged in as " + e.getName());
        while (true) {
            int input = getIntInput("What would you like to do?\n[1] Clock In\n[2] Clock Out\n[3] Edit Records\n[4] View Timestamps\n[5] Return to start", in);
            switch (input) {
                case 1://User clocks in
                    if (!e.timeCard.isClockedIn()) {
                        LocalTime time = checkValidTime("Please enter time (HH:mm) in 24 hour format:", in);
                        e.clockIn(time);
                    } else {
                        out.println("Already clocked in. Please clock out ...");
                    }
                    break;
                case 2://User clocks out
                    if (e.timeCard.isClockedIn()) {
                        LocalTime t = checkValidTime("Please enter clock out (HH:mm) in 24 hour format:", in);
                        e.clockOut(t);
                    } else {
                        out.println("Please clock in first before clocking out .. ");
                    }
                    break;
                case 3://User can edit previous timestamps
                    editRecords(e);
                    break;
                case 4:
                    while (true) {//user can print past/current timestamps
                        String response = getInput("Would you like to look at current or past timestamps? [c/p] (Press any other key to exit to home screen)", in);
                        switch (response.toUpperCase()) {
                            case "C":
                                out.println(e.timeCard.printCurrentTimestamps());
                                break;
                            case "P":
                                out.println(e.timeCard.printTimestampHistory());
                                break;
                            default:
                                return;
                        }
                    }
                default:
                    return;
            }
        }

    }


    //Allows an employee to edit clockin-clockout receipts as well as personal employee information
    private static void editRecords(Employee e) {

        //Following Changes are possible:
        //Name, Address, Salary/Wage
        //Time Stamps
        while (true){
            String response =  getInput("Would you like to edit timestamps or employee information? [t/e]", in);
            switch (response.toUpperCase()){
                case "T"://edit timestamps
                    getInput("Here are records from the current period:\n" + e, in); //prints current timestamps
                    for (int i = 0; i < e.timeCard.getTimestamps().size(); i++) {
                        out.println(i + "   " + e.timeCard.getTimestamps().get(i));
                    }
                    String input2 = getInput("Would you like to edit these records? [y/n]", in);
                    if(input2.equalsIgnoreCase("y")){
                        int index = getIntInput("Enter the index of the record you want to edit:", in);
                        while(true){
                            try{
                                alterTimeStamps(e, index); //allows user to alter timestamps
                                break;
                            } catch (Exception ex){
                                out.println("Please enter a valid index");
                            }
                        }
                    }
                    else{
                        out.println("Ok! Returning to main....");
                    }
                    break;
                case "E": //changing employee information
                    String input = getInput("Which field would you like to change? (Name [n], Address [a], and Wage/Salary [s] can be changed).\n" + e, in);
                    switch (input.toUpperCase()) {
                        case "N"://changing name of employee
                            String res = getInput("New Name:", in);
                            e.setName(res);
                            String input4 = getInput("Confirm: New Name is " + e.getName() + " [y/n]", in);
                            if (input4.equalsIgnoreCase("y")) {
                                out.println("Record Updated. Returning to main menu...");
                                return;
                            }
                        case "A"://changing address of employee
                            String response2 = getInput("New Address:", in);
                            e.setName(response2);
                            String input3 = getInput("Confirm: New Address is " + e.getName() + " [y/n]", in);
                            if (input3.equalsIgnoreCase("y")) {
                                out.println("Record Updated. Returning to main menu...");
                                return;
                            }
                        case "S"://changing salary/wage of employee
                            double newPayRate = getDoubleInput("New Salary:", in);
                            e.setPayRate(newPayRate);
                            String input5 = getInput("Confirm: New Salary is " + e.getPayRate() + " [y/n]", in);
                            if (input5.equalsIgnoreCase("y")) {
                                out.println("Record Updated. Returning to main menu...");
                                return;
                            }
                    }
            }
            String res2 = getInput("Would you like to exit? [y/n]", in);
            if(res2.equalsIgnoreCase("y")){
                return;
            }
        }

    }


    //Allows user to add/remove timestamp receipts. This also results in the number of minutes logged being changed.
    private static void alterTimeStamps(Employee e, int index)  throws IndexOutOfBoundsException{
        if(index < 0 || index > e.timeCard.getTimestamps().size()){
            throw new IndexOutOfBoundsException();
        }

        String input = getInput("Would you like to delete this record or add a new record? [a/d]", in);
        switch(input.toUpperCase()){
            case "A"://add time stamp
                LocalTime start = checkValidTime("Enter the time you want to check in:", in);
                LocalTime end = checkValidTime("Enter the time you want to check out:", in);
                LocalDate day = checkValidDate("Enter the date of the new timestamp:", in);
                e.timeCard.addTimeStamp(day, start, end, index);
                out.println("Timestamp Added! " + e.timeCard.getTimestamps().get(index) + "\n\n\n");
                break;
            case "D"://delete selected timestamp
                e.timeCard.removeTimeStamp(index);
                out.println("Record deleted" + e.timeCard.getTimestamps().get(index) + "\n\n\n");
                break;
        }



    }

    //Generates pay report. Pay report has start day of period, current date, number of days in period, and employee payout information.
    private static void generatePayReport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TimeCard.currentDate + "_" + payReportFile))) {
            String header = "PAYOUT REPORT\n" +
                    "____________________________________________________\n\n" +
                    "Start of Period: " + startDate + "\t\tDay of Filing : " + TimeCard.currentDate + "\n" +
                    "Total Days in Period  " + startDate.until(TimeCard.currentDate).getDays() + "\n\n\n" +
                    "EMPLOYEE PAYOUT\n" +
                    "____________________________________________________\n\n";
            writer.write(header);
            out.println(header);
            empSet.forEach(e -> {
                try {
                    writer.write(e.payReport());//write employee pay report to .payrpt file
                    out.println(e.payReport());
                    e.timeCard.moveRecordsToHistory();
                    e.storeMoneyData();
                    out.println("Money saved B)");
                } catch (IOException ex) {
                    out.println("Error while writing Payout Report ..");
                }
            });


        } catch (IOException ioe) {
            out.println("Error in writing to files. Returning to main...");
        }
    }



    //Allows user to provide employee information. This includes name, ID, DOB, address, salaried/wage. Instantiates an emmployee with given information
    public static Set<Employee> provideEmployeeDetails(Scanner in) {
        Set<Employee> empList = new HashSet<>();
        while (true) {
            String name = getInput("Please list the employee data...\nName (Last,First NO SPACE): ", in);
            String empId = getInput("EmpID: ", in);
            LocalDate dob = checkValidDate("DOB (YYYY-MM-dd): ", in);
            in.nextLine();

            String address = getLineInput("Address: ", in);

            boolean isSalaried = checkSalaried(in);

            double payoutAmt = getPayout(in);//gets the payrate for the given employee

            Employee e = isSalaried ?
                    Employee.salariedEmployee(name, empId, dob, address, payoutAmt)
                    : Employee.wageEmployee(name, empId, dob, address, payoutAmt);
            empList.add(e);//if the employee is salaried, then they are instantiated as a salaried employee. Otherwise they are a wage employee

            String addInput = getInput("Continue adding next employee [y/n] : ", in);
            if (!addInput.equalsIgnoreCase("Y")) {
                break;
            }
        }
        return empList;
    }

    //Helper method to get input as a whole line.
    private static String getLineInput(String prompt, Scanner in) {
        out.println(prompt);
        return in.nextLine();
    }


    //Asks user for pay rate information on prospective employee
    private static double getPayout(Scanner in) {
        while (true) {
            out.println("Payout amount(daily wage/yearly salary): ");
            try {
                double rate = in.nextDouble();
                return rate;
            } catch (Exception e) {
                out.println("Please enter a valid rate (just the number)");
            }
        }
    }

    //Asks the user if the prospective employee is salaried or wage-based.
    private static boolean checkSalaried(Scanner in) {

        while (true) {
            String input = getInput("Salaried/Wage? [s/w]:", in);
            switch (input.toUpperCase()) {
                case "S":
                    return true;
                case "W":
                    return false;
                default:
                    out.println("Please enter either \"s\" or \"w\" for a salaried or wage employee");
            }
        }
    }

    //Asks user for a date. Parses date and returns LocalDate object if given date is valid. Else asks user until valid date is given.
    private static LocalDate checkValidDate(String prompt, Scanner in) {
        while (true) {
            out.println(prompt);
            try {
                String input = in.next().strip();
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                out.println("Invalid Date Format, cannot parse. Please try again as per the format...");
            }
        }
    }

    //Asks user for a time. Parses date and returns LocalTime object if given time is valid. Else asks user until valid time is given.
    private static LocalTime checkValidTime(String prompt, Scanner in) {
        while (true) {
            out.println(prompt);
            try {
                LocalTime input = LocalTime.parse(in.next().strip());
                return LocalTime.parse(input.format(DateTimeFormatter.ISO_LOCAL_TIME));
            } catch (DateTimeParseException e) {
                out.println("Invalid Time Format, cannot parse. Please try again as per the format...");
            }
        }
    }


    //Gets top level input from user. Allows user to choose one of the 7 options provided.
    private static int getTopLevelInput(Scanner in) {
        while (true) {
            out.println("Welcome to the Badrinath Employee Database (BED). What would you like to do?");
            out.println("[1] Provide employee details\n[2] Generate Pay Report\n[3] View employee past records\n[4] Employee Sign-In\n[5] Start Day\n[6] End Day\n[7] Add Holiday\n[8] Exit");
            int input = in.nextInt();
            switch (input) {
                case 1, 2, 3, 4, 5, 6, 7, 8:
                    return input;
                default:
                    out.println("Please choose from the valid options");
            }
        }
    }


    //Saves all objects to a serialize file.
    public static void saveOnExit(Set<Employee> empSet) {
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile));
                ObjectOutputStream oosStartDate = new ObjectOutputStream(new FileOutputStream("StartDate-" + savefile));//to write start day of pay period
                ObjectOutputStream oosCurrentDate = new ObjectOutputStream(new FileOutputStream("CurrentDate-" + savefile));//to write current day of pay period.
        ) {
            oos.writeObject(empSet);//writes employee objects to a file.
            LocalDate dt = currentDate;
            oosCurrentDate.writeObject(dt);//writes start day of pay period

            LocalDate startDt = startDate;
            oosStartDate.writeObject(startDt);//writes current day of pay period

            out.println("System Exiting ... data is being saved");
        } catch (FileNotFoundException e) {
            out.println("Cannot find file to serialize ... Exiting without saving state ..");
        } catch (IOException e) {
            out.println("Cannot serialize ... Exiting without saving state ..");
        } finally {
            System.exit(0);
        }
    }


    //Loads saved employees to a set. Loads current day and start day of period to TimeCard.
    public static Set<Employee> loadOnStart() {
        Set<Employee> employees = new HashSet<>();
        // Restore state of the program from a saved file which is serialized
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
                ObjectInputStream oisCurrentDate = new ObjectInputStream(new FileInputStream("CurrentDate-" + savefile));
                ObjectInputStream oisStartDate = new ObjectInputStream(new FileInputStream("StartDate-" + savefile));
        ) {
            employees = (Set<Employee>) ois.readObject();
            LocalDate startDt = (LocalDate) oisStartDate.readObject();
            startDate = startDt;//gets start date
            LocalDate currentDt = (LocalDate) oisCurrentDate.readObject();
            currentDate = currentDt;//gets current date
            out.println("Start Date is " + ifNull(startDate, "_") + "    Current Date is " + ifNull(currentDate, "_"));
        } catch (FileNotFoundException fe) {
            out.println("Restore file not found .. Continuing ..");
        } catch (IOException e) {
            out.println("Restore file not deserializable .. Continuing ..");
        } catch (ClassNotFoundException e) {
            out.println("Restoration not possible .. Continuing ..");
        } finally {
            return employees;
        }
    }

    //Returns true if object is null, false otherwise
    private static Object ifNull(Object o, String expr) {
        return Objects.isNull(o) ? expr : o;
    }
}
