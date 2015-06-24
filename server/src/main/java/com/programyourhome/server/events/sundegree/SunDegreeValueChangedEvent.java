package com.programyourhome.server.events.sundegree;

import java.math.BigDecimal;

import com.programyourhome.server.events.ValueChangedTopicEvent;

public class SunDegreeValueChangedEvent extends ValueChangedTopicEvent<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public SunDegreeValueChangedEvent(final BigDecimal oldValue, final BigDecimal newValue) {
        super(oldValue, newValue);
    }

    @Override
    public Object getPayload() {
        final SunDirection direction = this.getOldValue().compareTo(this.getNewValue()) > 0 ? SunDirection.DOWN : SunDirection.UP;
        return this.createPayload("direction", direction, "degree", this.getNewValue());
    }

    @Override
    public String getTopic() {
        return "/topic/sensors/sunDegree/state";
    }

}
