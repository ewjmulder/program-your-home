package com.programyourhome.sensors.algorithmic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class SunriseSunsetForDate {

    private static final BigDecimal DEGREE_MAX = BigDecimal.valueOf(89.9);
    private static final BigDecimal DEGREE_MIN = DEGREE_MAX.negate();
    private static final BigDecimal DEGREE_STEP = BigDecimal.valueOf(0.1);

    private final LocalDate date;
    private final SortedMap<LocalTime, SunSnapshot> sunSnapshots;
    private final LocalTime astronomicalSunrise;
    private final LocalTime nauticalSunrise;
    private final LocalTime civilSunrise;
    private final LocalTime officialSunrise;
    private final LocalTime officialSunset;
    private final LocalTime civilSunset;
    private final LocalTime nauticalSunset;
    private final LocalTime astronomicalSunset;

    public SunriseSunsetForDate(final LocalDate date, final double longitude, final double latitude, final TimeZone timeZone) {
        this.date = date;
        this.sunSnapshots = new TreeMap<LocalTime, SunSnapshot>();

        final SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(new Location(longitude, latitude), TimeZone.getDefault());
        final Calendar calendarOfDate = new GregorianCalendar(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

        this.astronomicalSunrise = this.toLocalTime(calculator.getAstronomicalSunriseForDate(calendarOfDate));
        this.nauticalSunrise = this.toLocalTime(calculator.getNauticalSunriseForDate(calendarOfDate));
        this.civilSunrise = this.toLocalTime(calculator.getCivilSunriseForDate(calendarOfDate));
        this.officialSunrise = this.toLocalTime(calculator.getOfficialSunriseForDate(calendarOfDate));
        this.officialSunset = this.toLocalTime(calculator.getOfficialSunsetForDate(calendarOfDate));
        this.civilSunset = this.toLocalTime(calculator.getCivilSunsetForDate(calendarOfDate));
        this.nauticalSunset = this.toLocalTime(calculator.getNauticalSunsetForDate(calendarOfDate));
        this.astronomicalSunset = this.toLocalTime(calculator.getAstronomicalSunsetForDate(calendarOfDate));

        // Calculate the sunrise / sunset times for all possible degrees. Put them all together in a sorted map.
        for (BigDecimal degree = DEGREE_MIN; degree.compareTo(DEGREE_MAX) <= 0; degree = degree.add(DEGREE_STEP)) {
            final Calendar sunriseCalendar = SunriseSunsetCalculator.getSunrise(longitude, latitude, timeZone, calendarOfDate,
                    degree.doubleValue());
            if (sunriseCalendar != null) {
                final LocalTime sunriseTime = LocalTime.of(sunriseCalendar.get(Calendar.HOUR_OF_DAY), sunriseCalendar.get(Calendar.MINUTE));
                this.sunSnapshots.put(sunriseTime, new SunSnapshot(longitude, latitude, timeZone, date, sunriseTime, degree, true));
            }

            final Calendar sunsetCalendar = SunriseSunsetCalculator.getSunset(longitude, latitude, timeZone, calendarOfDate,
                    degree.doubleValue());
            if (sunsetCalendar != null) {
                final LocalTime sunsetTime = LocalTime.of(sunsetCalendar.get(Calendar.HOUR_OF_DAY), sunsetCalendar.get(Calendar.MINUTE));
                this.sunSnapshots.put(sunsetTime, new SunSnapshot(longitude, latitude, timeZone, date, sunsetTime, degree, false));
            }
        }

        // Find all 'missing' times and fill up with previous entries, so all hour-minute combinations will be present in the map.
        LocalTime lastKeyFound = this.sunSnapshots.firstKey();
        for (int hour = 0; hour <= 23; hour++) {
            for (int minute = 0; minute <= 59; minute++) {
                final LocalTime time = LocalTime.of(hour, minute);
                if (this.sunSnapshots.containsKey(time)) {
                    // Key is already present, save it as last found to be able to copy it's data later if needed.
                    lastKeyFound = time;
                } else {
                    // Key not in map, use value of last found key to 'fill up the gaps'.
                    this.sunSnapshots.put(time, this.sunSnapshots.get(lastKeyFound).cloneWithNewTime(time));
                }
            }
        }
    }

    private LocalTime toLocalTime(final String timeAsString) {
        final LocalTime localTime;
        if (timeAsString == null) {
            localTime = null;
        } else {
            localTime = LocalTime.parse(timeAsString);
        }
        return localTime;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public LocalTime getAstronomicalSunrise() {
        return this.astronomicalSunrise;
    }

    public LocalTime getNauticalSunrise() {
        return this.nauticalSunrise;
    }

    public LocalTime getCivilSunrise() {
        return this.civilSunrise;
    }

    public LocalTime getOfficialSunrise() {
        return this.officialSunrise;
    }

    public LocalTime getOfficialSunset() {
        return this.officialSunset;
    }

    public LocalTime getCivilSunset() {
        return this.civilSunset;
    }

    public LocalTime getNauticalSunset() {
        return this.nauticalSunset;
    }

    public LocalTime getAstronomicalSunset() {
        return this.astronomicalSunset;
    }

    public SunSnapshot getSunSnapshot(final LocalTime localTime) {
        return this.sunSnapshots.get(localTime);
    }

}
