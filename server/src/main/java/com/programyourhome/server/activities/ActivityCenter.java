package com.programyourhome.server.activities;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.ir.InfraRed;
import com.programyourhome.server.activities.model.PyhActivity;
import com.programyourhome.server.activities.model.PyhActivityImpl;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.Device;
import com.programyourhome.server.config.model.InfraRedActivityConfig;
import com.programyourhome.server.config.model.Key;
import com.programyourhome.server.config.model.Light;
import com.programyourhome.server.config.model.LightState;
import com.programyourhome.server.config.model.PhilipsHueActivityConfig;
import com.programyourhome.server.events.activities.ActivityChangedEvent;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

@Component
public class ActivityCenter {

    // TODO: Think about the detailed 'working' of activities: for now only one overall activity active is allowed.
    // Maybe split up per 'type' of activity, etc.

    @Inject
    private ServerConfigHolder configHolder;

    @Inject
    @Qualifier("PyhExecutor")
    private Executor taskExecutor;

    @Inject
    private PhilipsHue philipsHue;

    @Inject
    private InfraRed infraRed;

    @Inject
    private ApplicationEventPublisher eventPublisher;

    // TODO: naming: active / started / running / ongoing?
    private Activity activeActivity;

    @Value("${server.address}")
    private String host;

    @Value("${server.port}")
    private int port;

    @Value("${slide.host}")
    private String slideHost;

    public ActivityCenter() {
        this.activeActivity = null;
    }

    public synchronized boolean isActive(final Activity activity) {
        return activity.equals(this.activeActivity);
    }

    public synchronized boolean isNotActive(final Activity activity) {
        return !this.isActive(activity);
    }

    public synchronized PyhActivity createPyhActivity(final Activity activity) {
        final String defaultIcon = this.configHolder.getConfig().getActivitiesConfig().getDefaultIcon();
        return new PyhActivityImpl(activity, this.isActive(activity), "http://" + this.host + ":" + this.port + "", defaultIcon);
    }

    // TODO: probable race condition: quickly starting/stopping activities one after the other.
    // Possible solution: make (de)activation actions guaranteed in order / remove from active after all modules completed deactivation
    // Isn't that just what the task executor per module will take care of?
    public synchronized void startActivity(final Activity activity) {
	// FIXME: temp hack for Slide curtains
	if (activity.getName().equals("Curtains")) {
		try {
			String response = Request.Post("https://" + slideHost + "/rpc/Slide.SetPos").bodyString("{\"pos\": 1}", ContentType.APPLICATION_JSON).execute().returnContent().asString();
			System.out.println("Slide response: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
        if (this.isActive(activity)) {
            throw new IllegalStateException("Activity already active");
        }
        if (this.activeActivity != null) {
            // Another activity is currently active, so stop that one first.
            // TODO: Don't turn off / turn on overlapping devices, but only change channels where needed.
            this.stopActivity(this.activeActivity);
        }
        final PyhActivity oldValue = this.createPyhActivity(activity);
        this.activeActivity = activity;
        if (activity.getModules().getPhilipsHue() != null) {
            this.taskExecutor.execute(() -> this.activateHueModule(activity.getModules().getPhilipsHue()));
        }
        if (activity.getModules().getInfraRed() != null) {
            this.taskExecutor.execute(() -> this.activateIrModule(activity.getModules().getInfraRed()));
        }
        final PyhActivity newValue = this.createPyhActivity(activity);
        this.eventPublisher.publishEvent(new ActivityChangedEvent(oldValue, newValue));
    }

    public synchronized void stopActivity(final Activity activity) {
	// FIXME: temp hack for Slide curtains
	if (activity.getName().equals("Curtains")) {
		try {
			String response = Request.Post("https://" + slideHost + "/rpc/Slide.SetPos").bodyString("{\"pos\": 0}", ContentType.APPLICATION_JSON).execute().returnContent().asString();
			System.out.println("Slide response: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
        if (!this.isActive(activity)) {
            throw new IllegalStateException("Activity is not active");
        }
        final PyhActivity oldValue = this.createPyhActivity(activity);
        if (activity.getModules().getPhilipsHue() != null) {
            this.taskExecutor.execute(() -> this.deactivateHueModule(activity.getModules().getPhilipsHue()));
        }
        if (activity.getModules().getInfraRed() != null) {
            this.taskExecutor.execute(() -> this.deactivateIrModule(activity.getModules().getInfraRed()));
        }
        this.activeActivity = null;
        final PyhActivity newValue = this.createPyhActivity(activity);
        this.eventPublisher.publishEvent(new ActivityChangedEvent(oldValue, newValue));
    }

    private void activateHueModule(final PhilipsHueActivityConfig hueConfig) {
        for (final Light light : hueConfig.getLights()) {
            if (light.getTurnOff() != null) {
                this.philipsHue.turnOffLight(light.getId());
            } else {
                final LightState lightState = light.getState();
                if (lightState.getColorRGB() != null) {
                    this.philipsHue.dimToColorRGB(light.getId(), lightState.getDim(),
                            new Color(lightState.getColorRGB().getRed(), lightState.getColorRGB().getGreen(), lightState.getColorRGB().getBlue()));
                } else if (lightState.getColorXY() != null) {
                    this.philipsHue.dimToColorXY(light.getId(), lightState.getDim(), lightState.getColorXY().getX(), lightState.getColorXY().getY());
                } else if (lightState.getColorHueSaturation() != null) {
                    this.philipsHue.dimToColorHueSaturation(light.getId(), lightState.getDim(), lightState.getColorHueSaturation().getHue(),
                            lightState.getColorHueSaturation().getSaturation());
                } else if (lightState.getColorTemperature() != null) {
                    this.philipsHue.dimToColorTemperature(light.getId(), lightState.getDim(), lightState.getColorTemperature());
                } else if (lightState.getColorMood() != null) {
                    this.philipsHue.dimToMood(light.getId(), lightState.getDim(), Mood.valueOf(lightState.getColorMood().toString()));
                }
            }
        }
    }

    private void activateIrModule(final InfraRedActivityConfig irConfig) {
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

    private void deactivateHueModule(final PhilipsHueActivityConfig hueConfig) {
        for (final Light light : hueConfig.getLights()) {
            // TODO: what does it mean to deactivate an activity when talking about Hue?
            // Maybe go back to previous state? Or default state? Or default depending on time of day(light). -> yes, see general todo.
            // Only deactivate lights that were explicitely overridden by this activity. And re-activate overridden lights from other
            // (still active) activities.
        }
    }

    private void deactivateIrModule(final InfraRedActivityConfig irConfig) {
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

    private List<Activity> getActivitiesFromConfig() {
        return this.configHolder.getConfig().getActivitiesConfig().getActivities();
    }

    public Optional<Activity> findActivity(final int id) {
        return this.getActivitiesFromConfig().stream().filter(activity -> activity.getId() == id).findFirst();
    }

    public Optional<Activity> findActivity(final String name) {
        return this.getActivitiesFromConfig().stream().filter(activity -> activity.getName().equals(name)).findFirst();
    }

}
