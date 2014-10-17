package com.programyourhome.hue;

public enum LightType {

    HUE_FULL_COLOR_BULB(true, true, true, true),
    LIVING_COLORS(true, true, true, false),
    HUE_LUX_BULB(true, true, false, false),
    LIVING_WHITES_PLUG(true, false, false, false);

    private boolean onOffSwitch;
    private boolean dimmable;
    private boolean fullColor;
    private boolean moodLight;

    private LightType(final boolean onOffSwitch, final boolean dimmable, final boolean fullColor, final boolean moodLight) {
        this.onOffSwitch = onOffSwitch;
        this.dimmable = dimmable;
        this.fullColor = fullColor;
        this.moodLight = moodLight;
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

}
