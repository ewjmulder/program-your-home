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

    private final LocalDate today;
    private final SortedMap<LocalTime, SunSnapshot> sunSnapshots;
    private final SunriseSunsetCalculator calculator;

    public SunriseSunsetForDate(final LocalDate date, final double longitude, final double latitude, final TimeZone timeZone) {
        this.today = date;
        this.sunSnapshots = new TreeMap<LocalTime, SunSnapshot>();
        this.calculator = new SunriseSunsetCalculator(new Location(longitude, latitude), timeZone);

        // Calculate the sunrise / sunset times for all possible degrees. Put them all together in a sorted map.
        for (BigDecimal degree = DEGREE_MIN; degree.compareTo(DEGREE_MAX) <= 0; degree = degree.add(DEGREE_STEP)) {
            final Calendar sunriseCalendar = SunriseSunsetCalculator
                    .getSunrise(longitude, latitude, timeZone, this.getTodayAsCalendar(), degree.doubleValue());
            if (sunriseCalendar != null) {
                final LocalTime sunriseTime = LocalTime.of(sunriseCalendar.get(Calendar.HOUR_OF_DAY), sunriseCalendar.get(Calendar.MINUTE));
                this.sunSnapshots.put(sunriseTime, new SunSnapshot(longitude, latitude, timeZone, date, sunriseTime, degree, true));
            }

            final Calendar sunsetCalendar = SunriseSunsetCalculator
                    .getSunset(longitude, latitude, timeZone, this.getTodayAsCalendar(), degree.doubleValue());
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

    private Calendar getDayShiftAsCalendar(final DayShift dayShift) {
        Calendar calendar;
        if (dayShift == DayShift.YESTERDAY) {
            calendar = this.getYesterdayAsCalendar();
        } else if (dayShift == DayShift.TODAY) {
            calendar = this.getTodayAsCalendar();
        } else if (dayShift == DayShift.TOMORROW) {
            calendar = this.getTomorrowAsCalendar();
        } else {
            throw new IllegalStateException("Unknown day shift: '" + dayShift);
        }
        return calendar;
    }

    private Calendar getTodayAsCalendar() {
        return new GregorianCalendar(this.today.getYear(), this.today.getMonthValue() - 1, this.today.getDayOfMonth());
    }

    private Calendar getYesterdayAsCalendar() {
        return this.getDayShiftAsCalendar(-1);
    }

    private Calendar getTomorrowAsCalendar() {
        return this.getDayShiftAsCalendar(1);
    }

    private Calendar getDayShiftAsCalendar(final int amount) {
        final Calendar calendar = this.getTodayAsCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        return calendar;
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
        return this.today;
    }

    public LocalTime getAstronomicalSunrise() {
        return this.getAstronomicalSunrise(DayShift.TODAY);
    }

    public LocalTime getAstronomicalSunrise(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getAstronomicalSunriseForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getNauticalSunrise() {
        return this.getNauticalSunrise(DayShift.TODAY);
    }

    public LocalTime getNauticalSunrise(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getNauticalSunriseForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getCivilSunrise() {
        return this.getCivilSunrise(DayShift.TODAY);
    }

    public LocalTime getCivilSunrise(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getCivilSunriseForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getOfficialSunrise() {
        return this.getOfficialSunrise(DayShift.TODAY);
    }

    public LocalTime getOfficialSunrise(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getOfficialSunriseForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getOfficialSunset() {
        return this.getOfficialSunset(DayShift.TODAY);
    }

    public LocalTime getOfficialSunset(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getOfficialSunsetForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getCivilSunset() {
        return this.getCivilSunset(DayShift.TODAY);
    }

    public LocalTime getCivilSunset(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getCivilSunsetForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getNauticalSunset() {
        return this.getNauticalSunset(DayShift.TODAY);
    }

    public LocalTime getNauticalSunset(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getNauticalSunsetForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public LocalTime getAstronomicalSunset() {
        return this.getAstronomicalSunset(DayShift.TODAY);
    }

    public LocalTime getAstronomicalSunset(final DayShift dayShift) {
        return this.toLocalTime(this.calculator.getAstronomicalSunsetForDate(this.getDayShiftAsCalendar(dayShift)));
    }

    public SunSnapshot getSunSnapshot(final LocalTime localTime) {
        return this.sunSnapshots.get(localTime);
    }

}
