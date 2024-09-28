package main;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Utility function class. Stores loadHolidaysInPlace method.
 */
public class Utils {

    //Loads holidays already in place (Christmas 2024 and new year 2024).
    public static List<LocalDate> loadHolidaysInPlace() {
        List<LocalDate> holidays = Arrays.asList(
                "2024-12-25", "2024-01-01"
        ).stream().map(LocalDate::parse).toList();
        return holidays;
    }
}
