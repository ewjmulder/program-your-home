package com.programyourhome.server.events.sundegree;

import java.math.BigDecimal;

import com.programyourhome.server.events.ValueChangedEvent;

public class SunDegreeChangedEvent extends ValueChangedEvent<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public SunDegreeChangedEvent(final BigDecimal oldValue, final BigDecimal newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/sensors/sunDegree/angle";
    }

}
