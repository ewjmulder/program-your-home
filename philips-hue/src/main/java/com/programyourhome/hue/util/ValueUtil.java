package com.programyourhome.hue.util;

public class ValueUtil {

    private static final double MAX_BASIS_POINTS = 10000D;

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

    public static int basisPointsToBrightness(final int basisPoints) {
        return basisPointsToInt(basisPoints, BRIGHTNESS_MIN_VALUE, BRIGHTNESS_MAX_VALUE);
    }

    public static int basisPointsToHue(final int basisPoints) {
        return basisPointsToInt(basisPoints, HUE_MIN_VALUE, HUE_MAX_VALUE);
    }

    public static int basisPointsToSaturation(final int basisPoints) {
        return basisPointsToInt(basisPoints, SATURATION_MIN_VALUE, SATURATION_MAX_VALUE);
    }

    public static int basisPointsToColorTemperature(final int basisPoints) {
        return basisPointsToInt(basisPoints, COLOR_TEMPERATURE_MIN_VALUE, COLOR_TEMPERATURE_MAX_VALUE);
    }

    private static int basisPointsToInt(final int basisPoints, final int minValue, final int maxValue) {
        return minValue + (int) ((basisPoints / MAX_BASIS_POINTS) * (maxValue - minValue));
    }

}
