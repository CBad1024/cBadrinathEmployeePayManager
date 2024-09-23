import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map ;
class TimeCardTest {

    @org.junit.jupiter.api.Test
    void getOvertimeHours() {

        TimeCard t = new TimeCard();
        t.setLoggedMinutes(Map.of (
                "2024-01-05", 8*60l,
                "2024-01-06", 8*60l,
                "2024-01-07", 8*60l,
                "2024-01-02", 8*60l,
                "2024-01-03", 8*60l,
                "2024-01-04", 8*60l
                ,"2024-01-08", 8*60l)
        );

        assertEquals(16, t.getOvertimeHours());


        t.setLoggedMinutes(Map.of (
                "2024-01-01", 6*60l,
                "2024-01-03", 6*60l,
                "2024-01-04", 6*60l,
                "2024-01-05", 6*60l,
                "2024-01-06", 6*60l,
                "2024-01-02", 6*60l));

        assertEquals(0, t.getOvertimeHours());



        t.setLoggedMinutes(Map.of (
//                "2024-01-01", 0*60l,
//                "2024-01-03", 0*60l,
//                "2024-01-04", 8*60l,
                "2024-01-05", 9*60l,
                "2024-01-06", 8*60l,
                "2024-01-07", 8*60l,
                "2024-01-02", 8*60l));

        assertEquals(1, t.getOvertimeHours());


    }
}