package com.programyourhome.sensors.algorithmic;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;

@Component
public class AlgorithmicSunDegreeSensor implements SunDegreeSensor {

    // TODO: get the location from a properties file
    private final double longitude = 52.061886;
    private final double latitude = 5.27444;
    // TODO: make timezone configurable?
    private final TimeZone timeZone = TimeZone.getDefault();

    @Autowired
    private Clock clock;

    private SunriseSunsetForDate sunriseSunsetForDate;

    @PostConstruct
    public void init() {
        final LocalDate today = LocalDate.now();
        if (this.sunriseSunsetForDate == null || !this.sunriseSunsetForDate.getDate().equals(today)) {
            this.sunriseSunsetForDate = new SunriseSunsetForDate(today, this.longitude, this.latitude, this.timeZone);
        }
    }

    public static void main(final String[] args) {
        final LocalTime nineThirty = LocalTime.of(9, 30);
        final LocalTime tenThirty = LocalTime.of(10, 30);
        final LocalTime temp = nineThirty.minusHours(tenThirty.getHour());
        final LocalTime temp2 = temp.minusMinutes(tenThirty.getMinute());
        System.out.println("minus: " + temp2);
        System.out.println("now: " + LocalTime.now().truncatedTo(ChronoUnit.MINUTES));

        final Calendar calendarOfDate = new GregorianCalendar(2015, 0, 1);
        calendarOfDate.add(Calendar.DAY_OF_YEAR, -1);
        System.out.println("newCal: " + calendarOfDate.getTime());
    }

    // TODO: known limitation: today no such event, but maybe yesterday / tomorrow there is (fix?)
    // Test case: this.assertMarginYesterdayTodayTomorrow("00:00", "23:59", null, null, 1, true);
    protected boolean isEventWithinMargin(final Function<DayShift, LocalTime> eventFunction, final int margin) {
        if (Math.abs(margin) > 9) {
            throw new IllegalArgumentException("Margin must be in the range [-9, 9].");
        }
        boolean withinMargin;

        final LocalTime timeToday = eventFunction.apply(DayShift.TODAY);
        if (timeToday == null) {
            // With no time to compare to, it can never be within the margin.
            withinMargin = false;
        } else {
            final LocalTime now = LocalTime.now(this.clock).truncatedTo(ChronoUnit.MINUTES);
            withinMargin = this.countMinutesDifference(timeToday, now) <= margin;
            if (!withinMargin) {
                // Maybe we are around midnight, so we need the check the day before / after.
                final int minutesNow = this.countMinutes(now);
                if (minutesNow < margin) {
                    final LocalTime timeYesterday = eventFunction.apply(DayShift.YESTERDAY);
                    withinMargin = timeYesterday != null && this.isSameOrAfter(timeYesterday, now.minusMinutes(margin));
                } else if (this.countMinutesDifference(now, LocalTime.of(23, 59)) < margin) {
                    final LocalTime timeTomorrow = eventFunction.apply(DayShift.TOMORROW);
                    withinMargin = timeTomorrow != null && this.isSameOrBefore(timeTomorrow, now.plusMinutes(margin));
                }
            }
        }
        return withinMargin;
    }

    private boolean isSameOrAfter(final LocalTime time1, final LocalTime time2) {
        return time1.compareTo(time2) >= 0;
    }

    private boolean isSameOrBefore(final LocalTime time1, final LocalTime time2) {
        return time1.compareTo(time2) <= 0;
    }

    private int countMinutesDifference(final LocalTime time1, final LocalTime time2) {
        final LocalTime larger = time1.isAfter(time2) ? time1 : time2;
        final LocalTime smaller = time1.isAfter(time2) ? time2 : time1;
        final LocalTime subtracted = larger.minusHours(smaller.getHour()).minusMinutes(smaller.getMinute());
        final int numberOfMinutes = this.countMinutes(subtracted);
        return numberOfMinutes;
    }

    private int countMinutes(final LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    @Override
    public BigDecimal getSunDegree() {
        return this.sunriseSunsetForDate.getSunSnapshot(LocalTime.now()).getDegree();
    }

    @Override
    public boolean isAstronomicalSunrise(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.sunriseSunsetForDate.getAstronomicalSunrise(dayShift), margin);
    }

    @Override
    public boolean isNauticalSunrise(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.sunriseSunsetForDate.getNauticalSunrise(dayShift), margin);
    }

    @Override
    public boolean isCivilSunrise(final int margin) {
        return false;
    }

    @Override
    public boolean isOfficialSunrise(final int margin) {
        return false;
    }

    @Override
    public boolean isOfficialSunset(final int margin) {
        return false;
    }

    @Override
    public boolean isCivilSunset(final int margin) {
        return false;
    }

    @Override
    public boolean isNauticalSunset(final int margin) {
        return false;
    }

    @Override
    public boolean isAstronomicalSunset(final int margin) {
        return false;
    }
}
