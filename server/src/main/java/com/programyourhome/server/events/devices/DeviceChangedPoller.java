package com.programyourhome.server.events.devices;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.server.events.MapValueChangedPoller;

@Component
public class DeviceChangedPoller extends MapValueChangedPoller<Integer, PyhDevice> {

    @Autowired
    private InfraRed infraRed;

    public DeviceChangedPoller() {
        super(PyhDevice.class, DeviceChangedEvent.class);
    }

    @Override
    protected Collection<PyhDevice> getCurrentCollection() {
        return this.infraRed.getDevices();
    }

    @Override
    protected Integer getKey(final PyhDevice item) {
        return item.getId();
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable. Can be quite often, since this will actually take it's values from the in memory cache of the Ir module.
        return this.millis(500);
    }

}
