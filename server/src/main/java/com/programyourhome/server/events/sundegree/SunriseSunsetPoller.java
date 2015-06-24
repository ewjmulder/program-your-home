package com.programyourhome.server.events.sundegree;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.events.Poller;

@Component
public class SunriseSunsetPoller implements Poller {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SunDegreeSensor sunDegreeSensor;

    private final Set<SunriseSunsetEvent> publishedSunsetSunriseEvents;

    public SunriseSunsetPoller() {
        this.publishedSunsetSunriseEvents = new HashSet<>();
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable
        return this.seconds(30);
    }

    @Override
    public void poll() {
        final int margin = 0;
        final SunDegreeMoment moment;
        final SunriseSunset type;
        if (this.sunDegreeSensor.isOfficialSunrise(margin)) {
            moment = SunDegreeMoment.OFFICIAL;
            type = SunriseSunset.SUNRISE;
        } else if (this.sunDegreeSensor.isOfficialSunset(margin)) {
            moment = SunDegreeMoment.OFFICIAL;
            type = SunriseSunset.SUNSET;
        } else if (this.sunDegreeSensor.isCivilSunrise(margin)) {
            moment = SunDegreeMoment.CIVIL;
            type = SunriseSunset.SUNRISE;
        } else if (this.sunDegreeSensor.isCivilSunset(margin)) {
            moment = SunDegreeMoment.CIVIL;
            type = SunriseSunset.SUNSET;
        } else if (this.sunDegreeSensor.isNauticalSunrise(margin)) {
            moment = SunDegreeMoment.NAUTICAL;
            type = SunriseSunset.SUNRISE;
        } else if (this.sunDegreeSensor.isNauticalSunset(margin)) {
            moment = SunDegreeMoment.NAUTICAL;
            type = SunriseSunset.SUNSET;
        } else if (this.sunDegreeSensor.isAstronomicalSunrise(margin)) {
            moment = SunDegreeMoment.ASTRONOMICAL;
            type = SunriseSunset.SUNRISE;
        } else if (this.sunDegreeSensor.isAstronomicalSunset(margin)) {
            moment = SunDegreeMoment.ASTRONOMICAL;
            type = SunriseSunset.SUNSET;
        } else {
            moment = null;
            type = null;
        }
        if (moment != null && type != null) {
            final LocalDate today = LocalDate.now();
            final SunriseSunsetEvent event = new SunriseSunsetEvent(today, moment, type);
            // Prevent double events.
            if (!this.publishedSunsetSunriseEvents.contains(event)) {
                this.publishedSunsetSunriseEvents.add(event);
                this.eventPublisher.publishEvent(event);
            }
        }
    }
}
