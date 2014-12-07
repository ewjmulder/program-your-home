package com.programyourhome.server.activities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.programyourhome.config.Activity;
import com.programyourhome.config.Device;
import com.programyourhome.config.InfraRedConfig;
import com.programyourhome.config.Light;
import com.programyourhome.config.PhilipsHueConfig;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.ir.InfraRed;

@Component
public class ActivityCenter {

    // TODO: keep state somewhere, take that into account when activating activities.

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private PhilipsHue philipsHue;

    @Autowired
    private InfraRed infraRed;

    public void startActivity(final Activity activity) {
        // TODO: make modules into some kind of collection? hmm, but then how to call right activator?
        if (activity.getModules().getPhilipsHue() != null) {
            this.taskExecutor.execute(() -> this.activateHueModule(activity.getModules().getPhilipsHue()));
        }
        if (activity.getModules().getInfraRed() != null) {
            this.taskExecutor.execute(() -> this.activateIrModule(activity.getModules().getInfraRed()));
        }
    }

    private void activateHueModule(final PhilipsHueConfig hueConfig) {
        for (final Light light : hueConfig.getLights()) {
            if (light.getTurnOff() != null) {
                this.philipsHue.turnOffLight(light.getId());
            } else {
                if (light.getColorHueSaturation() != null) {
                    this.philipsHue.dimToColorHueSaturation(light.getId(), light.getDim(), light.getColorHueSaturation().getHue(), light
                            .getColorHueSaturation().getSaturation());
                } else if (light.getColorMood() != null) {
                    this.philipsHue.dimToMood(light.getId(), light.getDim(), Mood.valueOf(light.getColorMood().toString()));
                }
            }
        }
    }

    private void activateIrModule(final InfraRedConfig irConfig) {
        for (final Device device : irConfig.getDevices()) {
            if (device.getTurnOff() != null) {
                this.infraRed.turnOff(device.getId());
            } else {
                this.infraRed.turnOn(device.getId());
                if (device.getInput() != null) {
                    this.infraRed.setInput(device.getId(), device.getInput());
                }
            }
        }
    }

}
