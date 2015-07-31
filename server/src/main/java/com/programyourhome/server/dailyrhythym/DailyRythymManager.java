package com.programyourhome.server.dailyrhythym;

import java.awt.Color;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.util.streamex.StreamEx;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.common.util.ValueRangeUtil;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.KeyFrame;
import com.programyourhome.server.config.model.LightState;

@Component
public class DailyRythymManager {

    // TODO: move to properties of some kind
    // TODO: rewrite to class duration or so?
    private static final int UPDATE_INITIAL_DELAY = 2000;
    private static final long UPDATE_INTERVAL = 60 * 1000;

    @Autowired
    private ServerConfigHolder configHolder;

    @Autowired
    private SunDegreeSensor sunDegreeSensor;

    // TODO: use Spring scheduling annotations?
    @Autowired
    @Qualifier("PyhExecutor")
    private TaskScheduler rhythymScheduler;

    @Autowired
    private PhilipsHue philipsHue;

    @PostConstruct
    public void init() {
        this.rhythymScheduler.scheduleAtFixedRate(this::updateRhythym, DateUtils.addMilliseconds(new Date(), UPDATE_INITIAL_DELAY), UPDATE_INTERVAL);
    }

    public void updateRhythym() {
        final List<Integer> lightIds = this.configHolder.getConfig().getDailyRhythm().getLightsDailyRhythymConfig().getLights();
        final LocalTime currentTime = LocalTime.now();
        final RhythymSection activeSection = this.getActiveSection(currentTime);

        final Consumer<Integer> action = this.defineAction(currentTime, activeSection);

        if (this.shouldTurnOn(activeSection)) {
            lightIds.forEach(id -> action.accept(id));
        } else {
            lightIds.forEach(id -> this.philipsHue.turnOffLight(id));
        }
    }

    /**
     * Get the active section, that means the section that contains the provided time.
     * This method should always return a non-null result, since by design there will always be a active section.
     * Note: this logic assumes the times of the keyframes were previously validated to be in ascending order.
     *
     * @param time the time
     * @return the active section
     */
    private RhythymSection getActiveSection(final LocalTime time) {
        final List<KeyFrame> keyFrames = this.configHolder.getConfig().getDailyRhythm().getLightsDailyRhythymConfig().getKeyFrames();
        return StreamEx.of(keyFrames)
                .append(keyFrames.get(0)) // append the first item to 'close the loop'
                .pairMap(RhythymSection::new)
                .findFirst(section -> section.contains(time))
                .get(); // save to call get(), since the sections cover the full day, so there must always be a section that contains the provided time
    }

    private Consumer<Integer> defineAction(final LocalTime currentTime, final RhythymSection activeSection) {
        final Consumer<Integer> action;
        final double fraction = activeSection.getFraction(currentTime);
        final LightState fromLightState = activeSection.getFromLightState();
        final LightState toLightState = activeSection.getToLightState();
        final int dim = ValueRangeUtil.calculateIntFractionInRange(fraction, fromLightState.getDim(), toLightState.getDim());
        // Assumption: both from and to light state have the same color definition type set.
        if (fromLightState.getColorRGB() != null && toLightState.getColorRGB() != null) {
            final int red = ValueRangeUtil.calculateIntFractionInRange(fraction, fromLightState.getColorRGB().getRed(), toLightState.getColorRGB().getRed());
            final int green = ValueRangeUtil.calculateIntFractionInRange(fraction,
                    fromLightState.getColorRGB().getGreen(), toLightState.getColorRGB().getGreen());
            final int blue = ValueRangeUtil.calculateIntFractionInRange(fraction, fromLightState.getColorRGB().getBlue(), toLightState.getColorRGB().getBlue());
            action = id -> this.philipsHue.dimToColorRGB(id, dim, new Color(red, green, blue));
        } else if (fromLightState.getColorXY() != null && toLightState.getColorXY() != null) {
            final float x = (float) ValueRangeUtil.calculateFractionInRange(fraction, fromLightState.getColorXY().getX(), toLightState.getColorXY().getX());
            final float y = (float) ValueRangeUtil.calculateFractionInRange(fraction, fromLightState.getColorXY().getY(), toLightState.getColorXY().getY());
            action = id -> this.philipsHue.dimToColorXY(id, dim, x, y);
        } else if (fromLightState.getColorHueSaturation() != null && toLightState.getColorHueSaturation() != null) {
            final int hue = ValueRangeUtil.calculateIntFractionInRange(fraction,
                    fromLightState.getColorHueSaturation().getHue(), toLightState.getColorHueSaturation().getHue());
            final int saturation = ValueRangeUtil.calculateIntFractionInRange(fraction,
                    fromLightState.getColorHueSaturation().getSaturation(), toLightState.getColorHueSaturation().getSaturation());
            action = id -> this.philipsHue.dimToColorHueSaturation(id, dim, hue, saturation);
        } else if (fromLightState.getColorTemperature() != null && toLightState.getColorTemperature() != null) {
            final int temperature = ValueRangeUtil.calculateIntFractionInRange(fraction,
                    fromLightState.getColorTemperature(), toLightState.getColorTemperature());
            action = id -> this.philipsHue.dimToColorTemperature(id, dim, temperature);
        } else if (fromLightState.getColorMood() != null && toLightState.getColorMood() != null) {
            final int fromColorTemperature = Mood.valueOf(fromLightState.getColorMood().name()).getTemperature();
            final int toColorTemperature = Mood.valueOf(toLightState.getColorMood().name()).getTemperature();
            final int temperature = ValueRangeUtil.calculateIntFractionInRange(fraction, fromColorTemperature, toColorTemperature);
            action = id -> this.philipsHue.dimToColorTemperature(id, dim, temperature);
        } else {
            throw new IllegalArgumentException("No match found between from and to light state.");
        }
        return action;
    }

    private boolean shouldTurnOn(final RhythymSection section) {
        return true;
        // TODO: go fix
        // final TriggerType triggerType = section.getTrigger();
        // boolean turnOn;
        // if (triggerType == TriggerType.ALWAYS_ON) {
        // turnOn = true;
        // } else if (triggerType == TriggerType.ALWAYS_OFF) {
        // turnOn = false;
        // } else if (triggerType == TriggerType.SENSOR) {
        // final BigDecimal currentSunDegree = this.sunDegreeSensor.getSunDegree();
        // final BigDecimal turnLightsOnBelowSunDegree = this.configHolder.getConfig().getDailyRhythm().getTurnLightsOnBelowSunDegree();
        // turnOn = currentSunDegree.compareTo(turnLightsOnBelowSunDegree) < 0;
        // } else {
        // throw new IllegalStateException("Unknown trigger type: '" + triggerType + "'.");
        // }
        // return turnOn;
    }
}
