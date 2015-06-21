package com.programyourhome.server.events.activities;

import com.programyourhome.server.activities.model.PyhActivity;
import com.programyourhome.server.events.PyhEvent;

public abstract class ActivityToggledEvent extends PyhEvent {

    private static final long serialVersionUID = 1L;

    private final PyhActivity activity;

    public ActivityToggledEvent(final PyhActivity activity) {
        this.activity = activity;
    }

    public PyhActivity getActivity() {
        return this.activity;
    }

    @Override
    public boolean hasTopic() {
        return true;
    }

    @Override
    public String getTopic() {
        return "/topic/pyh/activities/" + this.activity.getId();
    }

    @Override
    public Object getPayload() {
        return this.activity;
    }

}
