package com.programyourhome.server.events;

import java.time.LocalDate;

import com.programyourhome.sensors.SunDegreeMoment;
import com.programyourhome.sensors.SunriseSunset;

public class SunDegreeEvent extends PyhEvent {

    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private final SunDegreeMoment moment;
    private final SunriseSunset type;

    public SunDegreeEvent(final LocalDate date, final SunDegreeMoment moment, final SunriseSunset type) {
        this.date = date;
        this.moment = moment;
        this.type = type;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public SunDegreeMoment getMoment() {
        return this.moment;
    }

    public SunriseSunset getType() {
        return this.type;
    }

}
