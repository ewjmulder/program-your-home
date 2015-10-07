package com.programyourhome.server.events.sundegree;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.events.ValueChangedPoller;

@Component
public class SunDegreeChangedPoller extends ValueChangedPoller<BigDecimal> {

    @Inject
    private SunDegreeSensor sunDegreeSensor;

    public SunDegreeChangedPoller() {
        super(BigDecimal.class, SunDegreeValueChangedEvent.class);
    }

    @Override
    protected BigDecimal getCurrentValue() {
        return this.sunDegreeSensor.getSunDegree();
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable
        return this.seconds(30);
    }

}
