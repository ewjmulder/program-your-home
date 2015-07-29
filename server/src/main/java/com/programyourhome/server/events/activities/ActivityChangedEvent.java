package com.programyourhome.server.events.activities;

import com.programyourhome.server.activities.model.PyhActivityImpl;
import com.programyourhome.server.events.ValueChangedEvent;

public class ActivityChangedEvent extends ValueChangedEvent<PyhActivityImpl> {

    private static final long serialVersionUID = 1L;

    public ActivityChangedEvent(final PyhActivityImpl oldValue, final PyhActivityImpl newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/pyh/activities/" + this.getNewValue().getId();
    }

}
