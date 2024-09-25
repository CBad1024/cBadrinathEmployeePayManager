package main;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.time.LocalDate.parse;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    public void testLoadingOfSerializedFile() {
        Main m = new Main() ;
        m.savefile = "EmployeeRecordsTest.ser";
        Set<Employee> empList = new HashSet(Arrays.asList(
                Employee.salariedEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 100),
                Employee.salariedEmployee("Aji", "E1234", parse("07/19/1975"), "Pershing", 1000),
                Employee.salariedEmployee("Manasa", "E1235", parse("07/19/1975"), "Pershing", 100)));
        m.saveOnExit(empList);

        Set<Employee> loaded = m.loadOnStart();
        loaded.forEach(System.out::println);

    }

    @Test
    public void testSets() {
        Set<Employee> a = new HashSet<>();
        a.add(Employee.salariedEmployee("Badri", "E123", parse("07/19/1975"), "Pershing", 100));
        a.add(Employee.salariedEmployee("Badri", "E1223", parse("07/19/1975"), "Pershing", 100));
        a.add(Employee.salariedEmployee("Badri", "E1233", parse("07/19/1975"), "Pershing", 100));


        Set<Employee> b = new HashSet<>();
        System.out.println(b);
        b.addAll(a);
        b.add(Employee.salariedEmployee("Badrinath", "E123", parse("07/19/1975"), "Pershing", 10000));
        System.out.println(b);

    }

    @Test
    void empDetailsTest(){
        Main.provideEmployeeDetails();

    }

    @Test
    void testLocalTime(){
        System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        System.out.println(LocalTime.parse("10:10").format(DateTimeFormatter.ISO_LOCAL_TIME));
    }

    @Test
    void checkStartDate() {

    }





}


