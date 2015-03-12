package com.programyourhome.sensors.algorithmic;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.programyourhome.sensors.SunDegreeSensor;

public class AlgorithmicSunDegreeSensorTest {

    private AlgorithmicSunDegreeSensor sensor;
    private Clock clockMock;

    @Before
    public void init() {
        this.sensor = new AlgorithmicSunDegreeSensor();
        this.clockMock = Mockito.mock(Clock.class);
        Mockito.when(this.clockMock.getZone()).thenReturn(ZoneId.of("Z"));
        ReflectionTestUtils.setField(this.sensor, "clock", this.clockMock);
    }

    @Test
    public void testIsEventWithinMarginToday() {
        this.assertMarginToday("00:00", "00:00", 0, true);
        this.assertMarginToday("00:00", "00:01", 0, false);
        this.assertMarginToday("23:59", "23:59", 0, true);
        this.assertMarginToday("23:59", "23:58", 0, false);
        this.assertMarginToday("12:00", "12:00", 0, true);
        this.assertMarginToday("12:00", "12:05", 5, true);
        this.assertMarginToday("12:00", "12:06", 5, false);
        this.assertMarginToday("12:00", "11:55", 5, true);
        this.assertMarginToday("12:00", "11:54", 5, false);
        this.assertMarginToday("12:00", "12:00", SunDegreeSensor.MAX_MARGIN, true);

        this.assertMarginToday("12:00", null, 5, false);
    }

    @Test
    public void testIsEventWithinMarginOverDayBoundary() {
        this.assertMarginYesterdayTodayTomorrow("00:00", "23:59", "12:00", "12:00", 1, true);
        this.assertMarginYesterdayTodayTomorrow("00:01", "23:59", "12:00", "12:00", 1, false);
        this.assertMarginYesterdayTodayTomorrow("00:00", "23:58", "12:00", "12:00", 1, false);
        this.assertMarginYesterdayTodayTomorrow("00:00", null, "12:00", "12:00", 1, false);
        this.assertMarginYesterdayTodayTomorrow("00:00", "23:59", null, null, 1, true);

        this.assertMarginYesterdayTodayTomorrow("23:59", "12:00", "12:00", "00:00", 1, true);
        this.assertMarginYesterdayTodayTomorrow("23:58", "12:00", "12:00", "00:00", 1, false);
        this.assertMarginYesterdayTodayTomorrow("23:59", "12:00", "12:00", "00:01", 1, false);
        this.assertMarginYesterdayTodayTomorrow("23:59", "12:00", "12:00", null, 1, false);
        this.assertMarginYesterdayTodayTomorrow("23:59", null, null, "00:00", 1, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMarginOutOfLowerBounds() {
        this.assertMarginToday("12:00", "12:00", -1, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMarginOutOfHigherBounds() {
        this.assertMarginToday("12:00", "12:00", SunDegreeSensor.MAX_MARGIN + 1, true);
    }

    private void assertMarginToday(final String timeNow, final String timeEvent, final int margin, final boolean eventWithinMargin) {
        this.assertMarginYesterdayTodayTomorrow(timeNow, timeEvent, timeEvent, timeEvent, margin, eventWithinMargin);
    }

    private void assertMarginYesterdayTodayTomorrow(final String timeNow, final String timeYesterday, final String timeToday, final String timeTomorrow,
            final int margin, final boolean eventWithinMargin) {
        this.setTimeNow(timeNow);
        Assert.assertEquals(eventWithinMargin, this.sensor.isEventWithinMargin(this.createEventFunction(timeYesterday, timeToday, timeTomorrow), margin));
    }

    private void setTimeNow(final String timeNow) {
        Mockito.when(this.clockMock.instant()).thenReturn(Instant.parse("2000-01-01T" + timeNow + ":00.00Z"));
    }

    private Function<DayShift, LocalTime> createEventFunction(final String timeYesterday, final String timeToday, final String timeTomorrow) {
        return dayShift -> {
            LocalTime time;
            if (dayShift == DayShift.YESTERDAY) {
                time = this.parseOrNull(timeYesterday);
            } else if (dayShift == DayShift.TODAY) {
                time = this.parseOrNull(timeToday);
            } else if (dayShift == DayShift.TOMORROW) {
                time = this.parseOrNull(timeTomorrow);
            } else {
                throw new IllegalStateException("Unknown day shift: " + dayShift);
            }
            return time;
        };
    }

    private LocalTime parseOrNull(final String time) {
        return time == null ? null : LocalTime.parse(time);
    }

}
