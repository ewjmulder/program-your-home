package com.programyourhome.sensors.algorithmic;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;

@Component
public class AlgorithmicSunDegreeSensor implements SunDegreeSensor {

    // TODO: get the location from a properties file
    private final double longitude = 52.061886;
    private final double latitude = 5.27444;
    // TODO: make timezone configurable?
    private final TimeZone timeZone = TimeZone.getDefault();

    private final Clock clock;
    private SunriseSunsetForDate currentSunriseSunset;

    public AlgorithmicSunDegreeSensor() {
        this.clock = Clock.systemDefaultZone();
    }

    /**
     * Give an up to date version of the sunrise sunset data.
     * Always use this method, do not access the field directly.
     *
     * @return the current sunrise sunset
     */
    private SunriseSunsetForDate giveCurrentSunriseSunset() {
        final LocalDate today = LocalDate.now();
        // If it is not yet initialized or if the contained date is not the current date, refresh the data for the current date.
        if (this.currentSunriseSunset == null || !this.currentSunriseSunset.getDate().equals(today)) {
            this.currentSunriseSunset = new SunriseSunsetForDate(today, this.longitude, this.latitude, this.timeZone);
        }
        return this.currentSunriseSunset;
    }

    protected boolean isEventWithinMargin(final Function<DayShift, LocalTime> eventFunction, final int margin) {
        if (margin < 0 || margin > MAX_MARGIN) {
            throw new IllegalArgumentException("Margin must be in the range [0, " + MAX_MARGIN + "].");
        }
        boolean withinMargin;

        final LocalTime timeToday = eventFunction.apply(DayShift.TODAY);
        final LocalTime now = LocalTime.now(this.clock).truncatedTo(ChronoUnit.MINUTES);
        withinMargin = timeToday != null && this.countMinutesDifference(timeToday, now) <= margin;
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
        return this.giveCurrentSunriseSunset().getSunSnapshot(LocalTime.now()).getDegree();
    }

    @Override
    public boolean isAstronomicalSunrise(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getAstronomicalSunrise(dayShift), margin);
    }

    @Override
    public boolean isNauticalSunrise(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getNauticalSunrise(dayShift), margin);
    }

    @Override
    public boolean isCivilSunrise(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getCivilSunrise(dayShift), margin);
    }

    @Override
    public boolean isOfficialSunrise(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getOfficialSunrise(dayShift), margin);
    }

    @Override
    public boolean isOfficialSunset(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getOfficialSunset(dayShift), margin);
    }

    @Override
    public boolean isCivilSunset(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getCivilSunset(dayShift), margin);
    }

    @Override
    public boolean isNauticalSunset(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getNauticalSunset(dayShift), margin);
    }

    @Override
    public boolean isAstronomicalSunset(final int margin) {
        return this.isEventWithinMargin(dayShift -> this.giveCurrentSunriseSunset().getAstronomicalSunset(dayShift), margin);
    }
}
