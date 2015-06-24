package com.programyourhome.server.events.lights;

import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.server.events.ValueChangedEvent;

public class LightChangedEvent extends ValueChangedEvent<PyhLight> {

    private static final long serialVersionUID = 1L;

    public LightChangedEvent(final PyhLight oldValue, final PyhLight newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/hue/lights/" + this.getNewValue().getId();
    }

}
