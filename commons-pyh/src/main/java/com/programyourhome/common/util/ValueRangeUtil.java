package com.programyourhome.common.util;

public class ValueRangeUtil {

    private static final int BASIS_POINTS_MIN_VALUE = 0;
    private static final int BASIS_POINTS_MAX_VALUE = 10000;

    private ValueRangeUtil() {
    }

    public static int calculateIntFractionInRange(final double fraction, final int minValue, final int maxValue) {
        return (int) Math.round(calculateFractionInRange(fraction, minValue, maxValue));
    }

    public static double calculateFractionInRange(final double fraction, final double minValue, final double maxValue) {
        return minValue + fraction * (maxValue - minValue);
    }

    public static int convertValueFromRangeToRange(final int value, final int minValueFrom, final int maxValueFrom, final int minValueTo, final int maxValueTo) {
        final double fraction = ((double) (value - minValueFrom)) / (maxValueFrom - minValueFrom);
        return calculateIntFractionInRange(fraction, minValueTo, maxValueTo);
    }

    public static int basisPointsToInt(final int basisPoints, final int minValue, final int maxValue) {
        return convertValueFromRangeToRange(basisPoints, BASIS_POINTS_MIN_VALUE, BASIS_POINTS_MAX_VALUE, minValue, maxValue);
    }

    public static int intToBasisPoints(final int intValue, final int minValue, final int maxValue) {
        return convertValueFromRangeToRange(intValue, minValue, maxValue, BASIS_POINTS_MIN_VALUE, BASIS_POINTS_MAX_VALUE);
    }

}
