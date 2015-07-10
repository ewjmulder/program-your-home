package com.programyourhome.common.time;

import java.time.LocalTime;

public class LocalTimeUtil {

    private LocalTimeUtil() {
    }

    public static boolean isEqualOrBefore(final LocalTime time1, final LocalTime time2) {
        return time1.equals(time2) || time1.isBefore(time2);
    }

    public static boolean isEqualOrAfter(final LocalTime time1, final LocalTime time2) {
        return time1.equals(time2) || time1.isAfter(time2);
    }

}
