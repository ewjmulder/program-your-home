package com.programyourhome.server.events.sundegree;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.events.Poller;

@Component
public class SunDegreePoller implements Poller {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SunDegreeSensor sunDegreeSensor;

    private final Set<SunsetSunriseEvent> publishedSunsetSunriseEvents;
    private BigDecimal lastPolledValue;

    public SunDegreePoller() {
        this.publishedSunsetSunriseEvents = new HashSet<>();
        this.lastPolledValue = null;
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable
        return 30 * 1000;
    }

    @Override
    public void poll() {
        this.pollForDegree();
        this.pollForSunsetSunrise();
    }

    // TODO: generic poller logic?
    private void pollForDegree() {
        final BigDecimal currentValue = this.sunDegreeSensor.getSunDegree();
        if (this.lastPolledValue != null && !currentValue.equals(this.lastPolledValue)) {
            this.eventPublisher.publishEvent(new SunDegreeValueChangedEvent(this.lastPolledValue, currentValue));
        }
        this.lastPolledValue = currentValue;
    }

    private void pollForSunsetSunrise() {
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
            final SunsetSunriseEvent event = new SunsetSunriseEvent(today, moment, type);
            // Prevent double events.
            if (!this.publishedSunsetSunriseEvents.contains(event)) {
                this.publishedSunsetSunriseEvents.add(event);
                this.eventPublisher.publishEvent(event);
            }
        }
    }
}
