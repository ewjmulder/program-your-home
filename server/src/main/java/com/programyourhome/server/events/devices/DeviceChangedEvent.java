package com.programyourhome.server.events.devices;

import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.server.events.ValueChangedEvent;

public class DeviceChangedEvent extends ValueChangedEvent<PyhDevice> {

    private static final long serialVersionUID = 1L;

    public DeviceChangedEvent(final PyhDevice oldValue, final PyhDevice newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/ir/devices/" + this.getNewValue().getId();
    }

}
