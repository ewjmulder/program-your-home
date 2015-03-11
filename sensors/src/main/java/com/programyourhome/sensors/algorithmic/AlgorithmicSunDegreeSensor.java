package com.programyourhome.sensors.algorithmic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;

@Component
public class AlgorithmicSunDegreeSensor implements SunDegreeSensor {

    // TODO: get the location from a properties file
    private final double longitude = 52.061886;
    private final double latitude = 5.27444;
    // TODO: make timezone configurable?
    private final TimeZone timeZone = TimeZone.getDefault();

    private SunriseSunsetForDate sunriseSunsetForDate;

    private void ensureSunriseSunsetData() {
        final LocalDate today = LocalDate.now();
        if (this.sunriseSunsetForDate == null || !this.sunriseSunsetForDate.getDate().equals(today)) {
            this.sunriseSunsetForDate = new SunriseSunsetForDate(today, this.longitude, this.latitude, this.timeZone);
        }
    }

    private boolean isWithinMargin(final LocalTime astronomicalSunrise) {
        // return LocalTime.now().???;
        return false;
    }

    @Override
    public BigDecimal getSunDegree() {
        this.ensureSunriseSunsetData();
        return this.sunriseSunsetForDate.getSunSnapshot(LocalTime.now()).getDegree();
    }

    @Override
    public boolean isAstronomicalSunrise(final int margin) {
        this.ensureSunriseSunsetData();
        return this.isWithinMargin(this.sunriseSunsetForDate.getAstronomicalSunrise());
    }

    @Override
    public boolean isNauticalSunrise(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }

    @Override
    public boolean isCivilSunrise(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }

    @Override
    public boolean isOfficialSunrise(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }

    @Override
    public boolean isOfficialSunset(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }

    @Override
    public boolean isCivilSunset(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }

    @Override
    public boolean isNauticalSunset(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }

    @Override
    public boolean isAstronomicalSunset(final int margin) {
        this.ensureSunriseSunsetData();
        return false;
    }
}
