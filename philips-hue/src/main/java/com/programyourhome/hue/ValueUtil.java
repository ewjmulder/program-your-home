package com.programyourhome.hue;

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

    public static int fractionToBrightness(final double fraction) {
        return fractionToInt(fraction, BRIGHTNESS_MIN_VALUE, BRIGHTNESS_MAX_VALUE);
    }

    public static int fractionToHue(final double fraction) {
        return fractionToInt(fraction, HUE_MIN_VALUE, HUE_MAX_VALUE);
    }

    public static int fractionToSaturation(final double fraction) {
        return fractionToInt(fraction, SATURATION_MIN_VALUE, SATURATION_MAX_VALUE);
    }

    public static int fractionToColorTemperature(final double fraction) {
        return fractionToInt(fraction, COLOR_TEMPERATURE_MIN_VALUE, COLOR_TEMPERATURE_MAX_VALUE);
    }

    private static int fractionToInt(final double fraction, final int minValue, final int maxValue) {
        return minValue + (int) (fraction * (maxValue - minValue));
    }

}
