package com.programyourhome.server.events;

import com.programyourhome.sensors.SunDegreeMoment;
import com.programyourhome.sensors.SunriseSunset;

public class SunDegreeEvent extends PyhEvent {

    private static final long serialVersionUID = 1L;

    private final SunDegreeMoment moment;
    private final SunriseSunset type;

    public SunDegreeEvent(final SunDegreeMoment moment, final SunriseSunset type) {
        this.moment = moment;
        this.type = type;
    }

    public SunDegreeMoment getMoment() {
        return this.moment;
    }

    public SunriseSunset getType() {
        return this.type;
    }

}
