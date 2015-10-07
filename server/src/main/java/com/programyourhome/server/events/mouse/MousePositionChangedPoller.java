package com.programyourhome.server.events.mouse;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.pc.PcInstructor;
import com.programyourhome.pc.model.PyhPoint;
import com.programyourhome.server.events.ValueChangedPoller;

@Component
public class MousePositionChangedPoller extends ValueChangedPoller<PyhPoint> {

    @Inject
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
