package com.programyourhome.server.events.activities;

import com.programyourhome.server.activities.model.PyhActivity;
import com.programyourhome.server.events.ValueChangedEvent;

public class ActivityChangedEvent extends ValueChangedEvent<PyhActivity> {

    private static final long serialVersionUID = 1L;

    public ActivityChangedEvent(final PyhActivity oldValue, final PyhActivity newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/pyh/activities/" + this.getNewValue().getId();
    }

}
