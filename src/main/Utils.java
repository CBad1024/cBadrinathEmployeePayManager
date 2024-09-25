package main;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static LocalDate startDate ; // Used for persistence and restoration

    public static List<LocalDate> loadHolidays(){
        List<LocalDate> holidays;

        try(BufferedReader reader = new BufferedReader(new FileReader("files/holidays.date"))) {

            holidays = reader.lines().map(e -> LocalDate.parse(e)).toList();
            return holidays;

        } catch (FileNotFoundException fnfe) {
            System.out.println("Holiday file not found");
        } catch (IOException e) {
            System.out.println("Could not read from file");
        } catch (DateTimeException dte){
            System.out.println("Invalid dates in holidays file");

        }
        return null;
    }

    public static List<LocalDate> loadHolidaysInPlace() {
        List<LocalDate> holidays = Arrays.asList(
                "2024-12-25", "2024-01-01"
        ).stream().map(LocalDate::parse).toList();
        return holidays;
    }
}
