package com.programyourhome.hue.util;

public class ValueUtil {

    private static final int BASIS_POINTS_MIN_VALUE = 0;
    private static final int BASIS_POINTS_MAX_VALUE = 10000;

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

    // Inner util functions.
    private static int convertValueFromRangeToRange(final int value, final int minValueFrom, final int maxValueFrom, final int minValueTo, final int maxValueTo) {
        final double fromFraction = ((double) (value - minValueFrom)) / (maxValueFrom - minValueFrom);
        return minValueTo + (int) (fromFraction * (maxValueTo - minValueTo));
    }

    private static int basisPointsToInt(final int basisPoints, final int minValue, final int maxValue) {
        return convertValueFromRangeToRange(basisPoints, BASIS_POINTS_MIN_VALUE, BASIS_POINTS_MAX_VALUE, minValue, maxValue);
    }

    private static int intToBasisPoints(final int intValue, final int minValue, final int maxValue) {
        return convertValueFromRangeToRange(intValue, minValue, maxValue, BASIS_POINTS_MIN_VALUE, BASIS_POINTS_MAX_VALUE);
    }

    // Convert from basis points to specific hue API values.
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

    // Convert from specific hue API values to basis points.
    public static int brightnessToBasisPoints(final int brightness) {
        return intToBasisPoints(brightness, BRIGHTNESS_MIN_VALUE, BRIGHTNESS_MAX_VALUE);
    }

    public static int hueToBasisPoints(final int hue) {
        return intToBasisPoints(hue, HUE_MIN_VALUE, HUE_MAX_VALUE);
    }

    public static int saturationToBasisPoints(final int saturation) {
        return intToBasisPoints(saturation, SATURATION_MIN_VALUE, SATURATION_MAX_VALUE);
    }

    public static int colorTemperatureToBasisPoints(final int colorTemperature) {
        return intToBasisPoints(colorTemperature, COLOR_TEMPERATURE_MIN_VALUE, COLOR_TEMPERATURE_MAX_VALUE);
    }

}
