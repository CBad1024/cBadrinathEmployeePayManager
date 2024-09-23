import java.util.*;
import java.lang.*;
import java.io.*;



public class Main {
    static String savefile = "EmployeeRecords.ser";
    static Scanner in = new Scanner(System.in);
    static Set<Employee> empSet = loadOnStart();
    public static void main(String[] args) {

        empSet.forEach(System.out::println);
        try {

            // Get User Input
            while (true){
                try{
                    int input = getTopLevelInput();

                    switch (input) {
                        case 1 -> {
                            Set<Employee> empSetNew = provideEmployeeDetails();
                            empSetNew.addAll(empSet);
                            empSet = new HashSet<>(empSetNew);
                        }
                        case 2 -> {
                            generatePayReport();
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
                            //Exit
                            saveOnExit(empSet);
                        }
                        case 6 ->{
                            //holiday
                            addHoliday();
                        }
                        case 7 ->{
                            //next day
                            endDay();
                        }


                    }




                } catch (InvalidInputException e){

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
        } catch (Exception e){
            if (e instanceof InterruptedException) {
                saveOnExit(empSet);
            }
            System.exit(0);


        }

    }

    private static void endDay() {

    }

    private static void addHoliday() {
        System.out.println("What day would you like to be a holiday?");
        System.out.println("Please enter a date in the form MM-d-yyyy");
        String date = in.next();

    }


    private static void empLogin() {
        System.out.println("Please enter your employee ID");
        String ID = in.next();
        for (Employee e : empSet){
            if (e.getEmpId().equals(ID)){
                empInterface(e);
                return;
            }
        }
        System.out.println("No such employee exists");
    }

    private static void empInterface(Employee e){
        System.out.println("Logged in as " + e.getName());


    }

    private static void loadPastRecords() {
        //fixme

    }

    private static void generatePayReport() {
        // FIXME
    }

    private static Set<Employee> provideEmployeeDetails() {
        Set<Employee> empList = new HashSet<>();
        while (true ) {
            System.out.println("Please list the employee data...\nName (Last,First NO SPACE): ");
            String name = in.next().strip();
            System.out.println("EmpID: ");
            String empId = in.next().strip();
            System.out.println("DOB (MM/dd/YYYY): ");
            String dob = checkValidDate(in.next().strip());
            System.out.println("Address: ");
            String address = in.next().strip();

            System.out.println("Salaried/Wage? [s/w]: ");
            boolean isSalaried = checkSalaried(in.next().strip());

            System.out.println("Payout amount(daily wage/salary): ");
            double payoutAmt = getPayout(in.next().strip());
            Employee e ;
            if (isSalaried) {
                e = Employee.salariedEmployee(name, empId, dob, address, payoutAmt);
            } else {
                e = Employee.wageEmployee(name, empId, dob, address, payoutAmt);
            }
            empList.add(e);
            System.out.println("Continue adding next employee [y/n] : ");
            String addInput = in.next().strip();
            if (!addInput.equalsIgnoreCase("Y")) {
                break;
            }
        }
        return empList;
    }

    private static double getPayout(String strip) {
        return 0; //FIXME
    }

    private static boolean checkSalaried(String strip) {
        return true; //FIXME
    }

    private static String checkValidDate(String strip) {
        return strip;
        //FIXME redo checkValidDate method
    }

    private static int getTopLevelInput() throws InvalidInputException{
        System.out.println("Welcome to the Badrinath Employee Database (BED). What would you like to do?");
        System.out.println("[1] Provide employee details\n[2] Generate Pay Report\n[3] View employee past records\n[4] Employee Sign-In\n[5] Exit\n[6] Add Holiday");
        int input = in.nextInt();
        switch(input){
            case 1, 2, 3, 4, 5, 6:
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
    public static void saveOnExit(Set<Employee> empSet){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile))){
            oos.writeObject(empSet);
            System.out.println("I am being called and I wrote data");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file to serialize ... Exiting without saving state ..");
        } catch (IOException e) {
            System.out.println("Cannot serialize ... Exiting without saving state ..");
        }
    }


    public static Set<Employee> loadOnStart() {
        Set<Employee> employees = new HashSet<>();
        // Restore state of the program from a saved file which is serialized
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile))) {
            employees = (Set<Employee>) ois.readObject();
        } catch (FileNotFoundException fe) {
            System.out.println("Restore file not found .. Continuing ..");
        } catch (IOException e) {
            System.out.println("Restore file not deserializable .. Continuing ..");
        } catch (ClassNotFoundException e) {
            System.out.println("Restoration not possible .. Continuing ..");
        }
        finally {
            return employees ;
        }
    }
}
