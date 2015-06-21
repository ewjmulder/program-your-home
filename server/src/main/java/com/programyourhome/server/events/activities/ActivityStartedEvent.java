package com.programyourhome.server.events.activities;

import com.programyourhome.server.activities.model.PyhActivity;

public class ActivityStartedEvent extends ActivityToggledEvent {

    private static final long serialVersionUID = 1L;

    public ActivityStartedEvent(final PyhActivity activity) {
        super(activity);
    }

}
