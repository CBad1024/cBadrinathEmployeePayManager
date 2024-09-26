package main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static java.time.LocalDate.parse;

import java.time.LocalDate;
import java.util.Map ;
import java.util.stream.Stream;

class TimeCardTest {

    static Stream<Arguments> overTimeHoursData() {
        return Stream.of(
                Arguments.of(Map.of (
                        parse("2024-01-05"), 8*60l,
                        parse("2024-01-06"), 8*60l,
                        parse("2024-01-07"), 8*60l,
                        parse("2024-01-02"), 8*60l,
                        parse("2024-01-03"), 8*60l,
                        parse("2024-01-04"), 8*60l
                        ,parse("2024-01-08"), 8*60l), 16), // Worked 16 hours greater than 40 hours per week
                Arguments.of(Map.of (
                        parse("2024-01-01"), 6*60l,
                        parse("2024-01-03"), 6*60l,
                        parse("2024-01-04"), 6*60l,
                        parse("2024-01-05"), 6*60l,
                        parse("2024-01-06"), 6*60l,
                        parse("2024-01-02"), 6*60l), 0), // Worked less than 8 hours per day and less than 40 hours per week
                Arguments.of(Map.of (
                        parse("2024-01-05"), 9*60l,
                        parse("2024-01-06"), 8*60l,
                        parse("2024-01-07"), 8*60l,
                        parse("2024-01-02"), 8*60l), 1), // Worked more hours on a day and less than 40 hours per week
                Arguments.of(Map.of(
                        parse("2024-01-05"), 10*60l,
                        parse("2024-01-06"), 7*60l,
                        parse("2024-01-07"), 8*60l,
                        parse("2024-01-08"), 8*60l,
                        parse("2024-01-09"), 8*60l), 2
                )
        );
    }

    @ParameterizedTest
    @MethodSource("overTimeHoursData")
    void getOvertimeHours(Map<LocalDate, Long> minuteLog, double expectedValue) {
        TimeCard t = new TimeCard();
        t.setLoggedMinutes(minuteLog);
        assertEquals(expectedValue, t.getOvertimeHours());
    }

    @Test
    void printHolidays(){
        TimeCard.holidays.forEach(System.out::println);
    }
}