import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @org.junit.jupiter.api.Test
    public void testLoadingOfSerializedFile() {
        Main m = new Main() ;
        m.savefile = "EmployeeRecordsTest.ser";
        Set<Employee> empList = new HashSet(Arrays.asList(
                Employee.salariedEmployee("Badri", "E123", "07/19/1975", "Pershing", 100),
                Employee.salariedEmployee("Aji", "E1234", "07/19/1975", "Pershing", 1000),
                Employee.salariedEmployee("Manasa", "E1235", "07/19/1975", "Pershing", 100)));
        m.saveOnExit(empList);

        Set<Employee> loaded = m.loadOnStart();
        loaded.forEach(System.out::println);

    }

    @org.junit.jupiter.api.Test
    public void testSets() {
        Set<Employee> a = new HashSet<>();
        a.add(Employee.salariedEmployee("Badri", "E123", "07/19/1975", "Pershing", 100));
        a.add(Employee.salariedEmployee("Badri", "E1223", "07/19/1975", "Pershing", 100));
        a.add(Employee.salariedEmployee("Badri", "E1233", "07/19/1975", "Pershing", 100));


        Set<Employee> b = new HashSet<>();
        System.out.println(b);
        b.addAll(a);
        b.add(Employee.salariedEmployee("Badrinath", "E123", "07/19/1975", "Pershing", 10000));
        System.out.println(b);

    }


}


