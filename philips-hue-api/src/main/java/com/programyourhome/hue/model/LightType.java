package com.programyourhome.hue.model;

import java.util.Arrays;

public enum LightType {

    // TODO: rename mood to reflect something like color temperature / licing whites?

    HUE_FULL_COLOR_BULB("LCT", true, true, true, true),
    LIVING_COLORS("LLC", true, true, true, false),
    HUE_LUX_BULB("LWB", true, true, false, false),
    LIVING_WHITES_PLUG("LWL", true, false, false, false);

    private String modelAbbreviation;
    private boolean onOffSwitch;
    private boolean dimmable;
    private boolean fullColor;
    private boolean moodLight;

    private LightType(final String modelAbbreviation, final boolean onOffSwitch, final boolean dimmable, final boolean fullColor, final boolean moodLight) {
        this.modelAbbreviation = modelAbbreviation;
        this.onOffSwitch = onOffSwitch;
        this.dimmable = dimmable;
        this.fullColor = fullColor;
        this.moodLight = moodLight;
    }

    public String getModelAbbreviation() {
        return this.modelAbbreviation;
    }

    public boolean isOnOffSwitch() {
        return this.onOffSwitch;
    }

    public boolean isDimmable() {
        return this.dimmable;
    }

    public boolean isFullColor() {
        return this.fullColor;
    }

    public boolean isMoodLight() {
        return this.moodLight;
    }

    // TODO: Should this method get a PHLight object? That implies a dependency on the Hue API's
    public static LightType fromModelAbbreviation(final String modelAbbreviation) {
        return Arrays.asList(LightType.values()).stream()
                .filter(type -> type.getModelAbbreviation().equals(modelAbbreviation))
                .findFirst()
                .get();
    }

}
