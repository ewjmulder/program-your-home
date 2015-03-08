package com.programyourhome.sensors.algorithmic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TimeZone;

public class SunSnapshot {

    private final double longitude;
    private final double latitude;
    private final TimeZone timeZone;
    private final LocalDate date;
    private final LocalTime time;
    private final BigDecimal degree;
    private final boolean rising;

    public SunSnapshot(final double longitude, final double latitude, final TimeZone timeZone, final LocalDate date, final LocalTime time,
            final BigDecimal degree,
            final boolean rising) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeZone = timeZone;
        this.date = date;
        this.time = time;
        this.degree = degree;
        this.rising = rising;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public BigDecimal getDegree() {
        return this.degree;
    }

    public boolean isRising() {
        return this.rising;
    }

    public SunSnapshot cloneWithNewTime(final LocalTime newTime) {
        return new SunSnapshot(this.longitude, this.latitude, this.timeZone, this.date, newTime, this.degree, this.rising);
    }

}
