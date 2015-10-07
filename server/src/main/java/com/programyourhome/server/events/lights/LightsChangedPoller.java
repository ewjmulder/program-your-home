package com.programyourhome.server.events.lights;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.server.events.MapValueChangedPoller;

@Component
public class LightsChangedPoller extends MapValueChangedPoller<Integer, PyhLight> {

    @Inject
    private PhilipsHue philipsHue;

    public LightsChangedPoller() {
        super(PyhLight.class, LightChangedEvent.class);
    }

    @Override
    protected Collection<PyhLight> getCurrentCollection() {
        return this.philipsHue.getLights();
    }

    @Override
    protected Integer getKey(final PyhLight item) {
        return item.getId();
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable. Can be quite often, since this will actually take it's values from the in memory cache of the Hue Java module.
        return this.millis(500);
    }

}
