package com.programyourhome.server.events.mouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.pc.PcInstructor;
import com.programyourhome.pc.model.PyhPoint;
import com.programyourhome.server.events.ValueChangedPoller;

@Component
public class MousePositionChangedPoller extends ValueChangedPoller<PyhPoint> {

    @Autowired
    private PcInstructor pcInstructor;

    public MousePositionChangedPoller() {
        super(PyhPoint.class, MousePositionValueChangedEvent.class);
    }

    @Override
    protected PyhPoint getCurrentValue() {
        return this.pcInstructor.getMousePosition();
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable
        return this.seconds(1);
    }

}
