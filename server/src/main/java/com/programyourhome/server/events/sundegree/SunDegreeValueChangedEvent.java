package com.programyourhome.server.events.sundegree;

import java.math.BigDecimal;

import com.programyourhome.server.events.ValueChangedTopicEvent;

public class SunDegreeValueChangedEvent extends ValueChangedTopicEvent<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public SunDegreeValueChangedEvent(final BigDecimal oldValue, final BigDecimal newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/sensors/sunDegree";
    }

}
