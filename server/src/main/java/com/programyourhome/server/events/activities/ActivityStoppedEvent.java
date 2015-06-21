package com.programyourhome.server.events.activities;

import com.programyourhome.server.activities.model.PyhActivity;

public class ActivityStoppedEvent extends ActivityToggledEvent {

    private static final long serialVersionUID = 1L;

    public ActivityStoppedEvent(final PyhActivity activity) {
        super(activity);
    }

}
