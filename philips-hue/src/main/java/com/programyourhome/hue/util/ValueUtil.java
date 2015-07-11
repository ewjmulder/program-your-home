package com.programyourhome.hue.util;

import com.programyourhome.common.util.ValueRangeUtil;

public class ValueUtil {

    public static final int BRIGHTNESS_MIN_VALUE = 0;
    public static final int BRIGHTNESS_MAX_VALUE = 254;
    public static final int HUE_MIN_VALUE = 0;
    public static final int HUE_MAX_VALUE = 65535;
    public static final int SATURATION_MIN_VALUE = 0;
    public static final int SATURATION_MAX_VALUE = 254;
    public static final int COLOR_TEMPERATURE_MIN_VALUE = 153;
    public static final int COLOR_TEMPERATURE_MAX_VALUE = 500;

    /**
     * Should never be instantiated, because this is a util class with only static methods.
     */
    private ValueUtil() {
    }

    // Convert from basis points to specific hue API values.
    public static int basisPointsToBrightness(final int basisPoints) {
        return ValueRangeUtil.basisPointsToInt(basisPoints, BRIGHTNESS_MIN_VALUE, BRIGHTNESS_MAX_VALUE);
    }

    public static int basisPointsToHue(final int basisPoints) {
        return ValueRangeUtil.basisPointsToInt(basisPoints, HUE_MIN_VALUE, HUE_MAX_VALUE);
    }

    public static int basisPointsToSaturation(final int basisPoints) {
        return ValueRangeUtil.basisPointsToInt(basisPoints, SATURATION_MIN_VALUE, SATURATION_MAX_VALUE);
    }

    public static int basisPointsToColorTemperature(final int basisPoints) {
        return ValueRangeUtil.basisPointsToInt(basisPoints, COLOR_TEMPERATURE_MIN_VALUE, COLOR_TEMPERATURE_MAX_VALUE);
    }

    // Convert from specific hue API values to basis points.
    public static int brightnessToBasisPoints(final int brightness) {
        return ValueRangeUtil.intToBasisPoints(brightness, BRIGHTNESS_MIN_VALUE, BRIGHTNESS_MAX_VALUE);
    }

    public static int hueToBasisPoints(final int hue) {
        return ValueRangeUtil.intToBasisPoints(hue, HUE_MIN_VALUE, HUE_MAX_VALUE);
    }

    public static int saturationToBasisPoints(final int saturation) {
        return ValueRangeUtil.intToBasisPoints(saturation, SATURATION_MIN_VALUE, SATURATION_MAX_VALUE);
    }

    public static int colorTemperatureToBasisPoints(final int colorTemperature) {
        return ValueRangeUtil.intToBasisPoints(colorTemperature, COLOR_TEMPERATURE_MIN_VALUE, COLOR_TEMPERATURE_MAX_VALUE);
    }

}
