package com.programyourhome.hue.util;

import org.apache.commons.lang3.StringUtils;

import com.philips.lighting.model.PHLightState;

public class ToStringUtil {

    public static String phLightStateToString(final PHLightState state) {
        return "On: " + StringUtils.leftPad("" + state.isOn(), 5) +
                ", bri: " + StringUtils.leftPad("" + state.getBrightness(), 3) +
                ", cm: " + StringUtils.leftPad("" + state.getColorMode(), 25) +
                ", hue: " + StringUtils.leftPad("" + state.getHue(), 5) +
                ", sat: " + StringUtils.leftPad("" + state.getSaturation(), 3) +
                ", x: " + StringUtils.leftPad("" + state.getX(), 6) +
                ", y: " + StringUtils.leftPad("" + state.getY(), 6) +
                ", ct: " + StringUtils.leftPad("" + state.getCt(), 3) +
                ", reach: " + StringUtils.leftPad("" + state.isReachable(), 5) +
                ", trans: " + StringUtils.leftPad("" + state.getTransitionTime(), 4) +
                ", alert: " + StringUtils.leftPad("" + state.getAlertMode(), 10) +
                ", effect: " + StringUtils.leftPad("" + state.getEffectMode(), 10);
    }

}
