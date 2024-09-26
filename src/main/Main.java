package main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.lang.*;
import java.io.*;
import static java.lang.System.out;


public class Main {
    static String savefile = "EmployeeRecords.ser";
    static String payReportFile = "PAYREPORT.payrpt";
    static Scanner in = new Scanner(System.in);
    static Set<Employee> empSet = loadOnStart();
    static LocalDate startDate;
    static boolean dayHasStarted = (startDate != null);

    public static void main(String[] args) {
        in.useDelimiter("\\n");

        // FIXME - Remove this statement before submitting
        empSet.forEach(System.out::println);
        try {
            while (true) {
                    int input = getTopLevelInput(in);
                    switch (input) {
                        case 1 -> {
                            Set<Employee> empSetNew = provideEmployeeDetails(in);
                            empSetNew.addAll(empSet);
                            empSet = new HashSet<>(empSetNew);
                        }
                        case 2 -> {
                            generatePayReport();
                        }
                        case 3 -> {
                            out.println("Please open the files that end in .payrpt format ..");
                        }
                        case 4 -> {
                            empLogin(in);
                        }
                        case 5 -> {
                            startDay(in);
                        }
                        case 6 -> {
                            endDay(in);
                        }
                        case 7 -> {
                            addHoliday(in);
                        }
                        case 8 -> {
                            saveOnExit(empSet);
                        }
                    }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                saveOnExit(empSet);
            }
        }
    }

    private static void startDay(Scanner in) {
        if (dayHasStarted) {
            out.println("You have already started the day. ");
            return;
        }
        if (startDate != null) {
            TimeCard.currentDate = getNextWorkingDay(TimeCard.currentDate);
            getInput("Starting Day [ " + TimeCard.currentDate.format(DateTimeFormatter.ISO_DATE) + " ] : Press Any Key ..", in);
            dayHasStarted = true;
        } else {
            while (true) {
                LocalDate date = checkValidDate(" Start Day [YYYY-MM-dd] : ", in);
                if (TimeCard.holidays.contains(date)) {
                    out.println("That day is a holiday. Please enter a non-holiday...");
                } else {
                    startDate = date;
                    TimeCard.currentDate = date;
                    dayHasStarted = true;
                    break;
                }
            }
        }
        empSet.forEach(e ->
        {
            e.timeCard.addZeroMinutes(); //For making sure that every employee is registered as having some activity each day (for wage calculation purposes)
        });
    }

    private static LocalDate getNextWorkingDay(LocalDate dt) {
        while (true) {
            LocalDate nextDay = dt.plusDays(1);
            if (TimeCard.holidays.contains(nextDay)) {
                dt = nextDay;
            } else {
                return nextDay;
            }
        }
    }

    private static void endDay(Scanner in) {
        if (!dayHasStarted) {
            out.println("Day has already ended. Please start the day first ..");
            return;
        }
        String input = getInput("Would you like to like to end the day [y/n]? Today is " + TimeCard.currentDate, in);
        switch (input.toUpperCase()) {
            case "Y":
                dayHasStarted = false;
                break;
            default:
                out.println("OK! Returning to homescreen...");
        }

    }

    private static void addHoliday(Scanner in) {
        String input = getInput("This application comes with an inbuilt holiday calendar. Would you like to add to this calendar or use it as is? [y/n]", in);

        Set<LocalDate> dateSet = new HashSet<>();
        dateSet.addAll(TimeCard.holidays);

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
        TimeCard.holidays = dateSet.stream().toList();
    }

    private static String getInput(String prompt, Scanner in) {
        out.println(prompt);
        return in.next().strip();
    }

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

    private static void empLogin(Scanner  in) {
        if (!dayHasStarted) {
            out.println("Before checking in as an employee, Start the day ..");
            return;
        }
        String ID = getInput("Please enter your employee ID", in);
        Employee emp = empSet.stream().filter(e -> e.getEmpId().equalsIgnoreCase(ID)).findFirst().orElse(null);
        if (emp == null) {
            out.println("No such employee exists");
        } else {
            empInterface(emp, in);
        }
    }

    private static void empInterface(Employee e, Scanner in) {
        out.println("Logged in as " + e.getName());
        while (true) {
            int input = getIntInput("What would you like to do?\n[1] Clock In\n[2] Clock Out\n[3] Edit Records\n[4] View Timestamps\n[5] Return to start", in);
            switch (input) {
                case 1:
                    if (!e.timeCard.isClockedIn()) {
                        LocalTime time = checkValidTime("Please enter time (HH:mm) in 24 hour format:", in);
                        e.clockIn(time);
                    } else {
                        out.println("Already clocked in. Please clock out ...");
                    }
                    break;
                case 2:
                    if (e.timeCard.isClockedIn()) {
                        LocalTime t = checkValidTime("Please enter clock out (HH:mm) in 24 hour format:", in);
                        e.clockOut(t);
                    } else {
                        out.println("Please clock in first before clocking out .. ");
                    }
                    break;
                case 3:
                    editRecords(e);
                    break;
                case 4:
                    while (true) {
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

    private static void editRecords(Employee e) {

        //Following Changes are possible:
        //Name, Address, Salary/Wage
        //Time Stamps
        String response =  getInput("Would you like to edit timestamps or employee information? [t/e]", in);
        switch (response.toUpperCase()){
            case "T":
                String input1 = getInput("Here are records from the current period:\n" + e, in);
                int i = 0;
                e.timeCard.getTimestamps().forEach(el -> {
                    out.println(i + " " + el);
                });
                String input2 = getInput("Would you like to edit these records? [y/n]", in);
                if(input2.equalsIgnoreCase("y")){
                    int index = getIntInput("Enter the index of the record you want to edit:", in);
                    while(true){
                        try{
                            alterTimeStamps(e, 1);//FIXME
                        } catch (Exception ex){
                            out.println("Please enter a valid index");
                        }
                    }
                }
                else{
                    out.println("Ok! Returning to main....");
                }
                break;
            case "E":
                String input = getInput("Which field would you like to change? (Name [n] and Address [a] can be changed).\n" + e, in);
                switch (input.toUpperCase()) {
                    case "N":
                        String res = getInput("New Name:", in);
                        e.setName(res);
                        String input4 = getInput("Confirm: New Name is " + e.getName() + " [y/n]", in);
                        if (input4.equalsIgnoreCase("y")) {
                            out.println("Record Updated. Returning to main menu...");
                            return;
                        }
                    case "A":
                        String response2 = getInput("New Address:", in);
                        e.setName(response2);
                        String input3 = getInput("Confirm: New Address is " + e.getName() + " [y/n]", in);
                        if (input3.equalsIgnoreCase("y")) {
                            out.println("Record Updated. Returning to main menu...");
                            return;
                        }


                }
        }
    }

    private static void alterTimeStamps(Employee e, int index) {
//        e.timeCard.setTimeStamps
    }

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
            empSet.forEach(e ->
            {
                try {
                    writer.write(e.payReport());
                    out.println(e.payReport());
                    e.timeCard.moveRecordsToHistory();
                    e.storeMoneyData();
                } catch (IOException ex) {
                    out.println("Error while writing Payout Report ..");
                }
            });


        } catch (IOException ioe) {
            out.println("Error in writing to files. Returning to main...");
        }
    }


    public static Set<Employee> provideEmployeeDetails(Scanner in) {
        Set<Employee> empList = new HashSet<>();
        while (true) {
            String name = getInput("Please list the employee data...\nName (Last,First NO SPACE): ", in);
            String empId = getInput("EmpID: ", in);
            LocalDate dob = checkValidDate("DOB (YYYY-MM-dd): ", in);

            in.nextLine();

            String address = getFullInput("Address: ");

            boolean isSalaried = checkSalaried();

            double payoutAmt = getPayout();

            Employee e = isSalaried ?
                    Employee.salariedEmployee(name, empId, dob, address, payoutAmt)
                    : Employee.wageEmployee(name, empId, dob, address, payoutAmt);
            empList.add(e);

            out.println("Continue adding next employee [y/n] : ");
            String addInput = in.next().strip();
            if (!addInput.equalsIgnoreCase("Y")) {
                break;
            }
        }
        return empList;
    }

    private static String getFullInput(String prompt) {
        out.println(prompt);
        return in.nextLine();
    }


    private static double getPayout() {
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

    private static boolean checkSalaried() {

        while (true) {
            out.println("Salaried/Wage? [s/w]: ");
            String input = in.next().strip();
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


    public static void saveOnExit(Set<Employee> empSet) {
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile));
                ObjectOutputStream oosStartDate = new ObjectOutputStream(new FileOutputStream("StartDate-" + savefile));
                ObjectOutputStream oosCurrentDate = new ObjectOutputStream(new FileOutputStream("CurrentDate-" + savefile));
        ) {
            oos.writeObject(empSet);
            LocalDate dt = TimeCard.currentDate;
            oosCurrentDate.writeObject(dt);

            LocalDate startDate = Main.startDate;
            oosStartDate.writeObject(startDate);

            out.println("System Exiting ... data is being saved");
        } catch (FileNotFoundException e) {
            out.println("Cannot find file to serialize ... Exiting without saving state ..");
        } catch (IOException e) {
            out.println("Cannot serialize ... Exiting without saving state ..");
        } finally {
            System.exit(0);
        }
    }


    public static Set<Employee> loadOnStart() {
        Set<Employee> employees = new HashSet<>();
        // Restore state of the program from a saved file which is serialized
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
                ObjectInputStream oisCurrentDate = new ObjectInputStream(new FileInputStream("CurrentDate-" + savefile));
                ObjectInputStream oisStartDate = new ObjectInputStream(new FileInputStream("StartDate-" + savefile));
        ) {
            employees = (Set<Employee>) ois.readObject();
            LocalDate startDate = (LocalDate) oisStartDate.readObject();
            Main.startDate = startDate;
            LocalDate currentDate = (LocalDate) oisCurrentDate.readObject();
            TimeCard.currentDate = currentDate;
            out.println("Start Date is " + ifNull(Main.startDate, "_") + "    Current Date is " + ifNull(TimeCard.currentDate, "_"));
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

    private static Object ifNull(Object o, String expr) {
        return Objects.isNull(o) ? expr : o;
    }
}
