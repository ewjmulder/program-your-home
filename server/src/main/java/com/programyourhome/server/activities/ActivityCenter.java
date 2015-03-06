package com.programyourhome.server.activities;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.ir.InfraRed;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.Device;
import com.programyourhome.server.config.model.InfraRedConfig;
import com.programyourhome.server.config.model.Key;
import com.programyourhome.server.config.model.Light;
import com.programyourhome.server.config.model.PhilipsHueConfig;

@Component
public class ActivityCenter {

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private PhilipsHue philipsHue;

    @Autowired
    private InfraRed infraRed;

    // TODO: naming: active / started / running / ongoing?
    private final Set<Activity> activeActivities;

    public ActivityCenter() {
        this.activeActivities = new HashSet<Activity>();
    }

    public synchronized boolean isActive(final Activity activity) {
        return this.activeActivities.contains(activity);
    }

    // TODO: probable race condition: quickly starting/stopping activities one after the other.
    // Possible solution: make (de)activation actions guaranteed in order / remove from active after all modules completed deactivation
    // Isn't that just what the task executor per module will take care of?
    public synchronized void startActivity(final Activity activity) {
        if (this.isActive(activity)) {
            throw new IllegalStateException("Activity already active");
        }
        this.activeActivities.add(activity);
        if (activity.getModules().getPhilipsHue() != null) {
            this.taskExecutor.execute(() -> this.activateHueModule(activity.getModules().getPhilipsHue()));
        }
        if (activity.getModules().getInfraRed() != null) {
            this.taskExecutor.execute(() -> this.activateIrModule(activity.getModules().getInfraRed()));
        }
    }

    public synchronized void stopActivity(final Activity activity) {
        if (!this.isActive(activity)) {
            throw new IllegalStateException("Activity is not active");
        }
        this.activeActivities.remove(activity);
        if (activity.getModules().getPhilipsHue() != null) {
            this.taskExecutor.execute(() -> this.deactivateHueModule(activity.getModules().getPhilipsHue()));
        }
        if (activity.getModules().getInfraRed() != null) {
            this.taskExecutor.execute(() -> this.deactivateIrModule(activity.getModules().getInfraRed()));
        }
    }

    private void activateHueModule(final PhilipsHueConfig hueConfig) {
        for (final Light light : hueConfig.getLights()) {
            if (light.getTurnOff() != null) {
                this.philipsHue.turnOffLight(light.getId());
            } else {
                if (light.getColorRGB() != null) {
                    this.philipsHue.dimToColorRGB(light.getId(), light.getDim(),
                            new Color(light.getColorRGB().getRed(), light.getColorRGB().getGreen(), light.getColorRGB().getBlue()));
                } else if (light.getColorXY() != null) {
                    this.philipsHue.dimToColorXY(light.getId(), light.getDim(), light.getColorXY().getX(), light.getColorXY().getY());
                } else if (light.getColorHueSaturation() != null) {
                    this.philipsHue.dimToColorHueSaturation(light.getId(), light.getDim(), light.getColorHueSaturation().getHue(), light
                            .getColorHueSaturation().getSaturation());
                } else if (light.getColorTemperature() != null) {
                    this.philipsHue.dimToColorTemperature(light.getId(), light.getDim(), light.getColorTemperature());
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
        for (final Key key : irConfig.getKeys()) {
            this.infraRed.pressKeyOnRemote(key.getDevice(), key.getId());
        }
    }

    private void deactivateHueModule(final PhilipsHueConfig hueConfig) {
        for (final Light light : hueConfig.getLights()) {
            // TODO: what does it mean to deactivate an activity when talking about Hue?
            // Maybe go back to previous state? Or default state? Or default depending on time of day(light). -> yes, see general todo.
            // Only deactivate lights that were explicitely overridden by this activity. And re-activate overridden lights from other
            // (still active) activities.
        }
    }

    private void deactivateIrModule(final InfraRedConfig irConfig) {
        for (final Device device : irConfig.getDevices()) {
            if (device.getTurnOff() != null) {
                // TODO: Do not turn on devices again? probably best if there is a conflict in activities
                // for devices that you must stop the other activity (or for all?) lighting might have a use.
                // How about this: conflicts will just be overridden by default and not restored. Can always change in the future.
            } else {
                // The devices that should be turned on for this activity, now should be turned off.
                this.infraRed.turnOff(device.getId());
            }
        }
    }

}
