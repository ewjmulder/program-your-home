package com.programyourhome.server.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.sensors.DaylightSensor;
import com.programyourhome.server.rules.model.PyhRule;

@Component
public class DaylightLightingRule implements PyhRule {

    @Autowired
    private DaylightSensor daylightSensor;

    @Override
    public String getName() {
        return "Daylight Lighting";
    }

    @Override
    public String getDescription() {
        return "Control the light depending on the amount of daylight";
    }

    @Override
    public boolean isTriggered() {
        // This rule always applies, since it's more of a general 'law'.
        return true;
    }

    @Override
    public void executeAction() {
        this.daylightSensor.getDaylight();
    }
}
