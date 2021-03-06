package com.programyourhome.server.events.sundegree;

import java.time.LocalDate;

import com.programyourhome.server.events.PyhTopicEvent;

public class SunriseSunsetEvent extends PyhTopicEvent {

    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private final SunDegreeMoment moment;
    private final SunriseSunset type;

    public SunriseSunsetEvent(final LocalDate date, final SunDegreeMoment moment, final SunriseSunset type) {
        this.date = date;
        this.moment = moment;
        this.type = type;
    }

    @Override
    public String getTopic() {
        return "/topic/sensors/sunDegree/event";
    }

    @Override
    public Object getPayload() {
        return this.createPayload("moment", this.moment, "type", this.type);
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
