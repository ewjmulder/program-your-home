package com.programyourhome.server.events.mouse;

import com.programyourhome.pc.model.PyhPoint;
import com.programyourhome.server.events.ValueChangedEvent;

public class MousePositionValueChangedEvent extends ValueChangedEvent<PyhPoint> {

    private static final long serialVersionUID = 1L;

    public MousePositionValueChangedEvent(final PyhPoint oldValue, final PyhPoint newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/pc/mouse/position";
    }

}
