package com.programyourhome.server.events.sundegree;

import java.math.BigDecimal;

import com.programyourhome.server.events.ValueChangedEvent;

public class SunDegreeValueChangedEvent extends ValueChangedEvent<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public SunDegreeValueChangedEvent(final BigDecimal oldValue, final BigDecimal newValue) {
        super(oldValue, newValue);
    }

}
