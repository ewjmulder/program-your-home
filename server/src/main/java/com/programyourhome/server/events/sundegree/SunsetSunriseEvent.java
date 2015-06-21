package com.programyourhome.server.events.sundegree;

import java.time.LocalDate;

import com.programyourhome.server.events.PyhEvent;

public class SunsetSunriseEvent extends PyhEvent {

    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private final SunDegreeMoment moment;
    private final SunriseSunset type;

    public SunsetSunriseEvent(final LocalDate date, final SunDegreeMoment moment, final SunriseSunset type) {
        this.date = date;
        this.moment = moment;
        this.type = type;
    }

    @Override
    public boolean hasTopic() {
        // TODO: create topic for this.
        return false;
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
