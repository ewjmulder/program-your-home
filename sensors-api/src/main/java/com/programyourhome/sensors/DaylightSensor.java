package com.programyourhome.sensors;

public interface DaylightSensor {

    /**
     * Gets the amount of daylight as a number of basis points [0, 10000].
     * A daylight value of 0 is absolute darkness and 10000 is the brightest daylight.
     * Of course this will always be an approximation according to the possibilities of the
     * actual sensor. The returned values should scale linear, so 5000 should be somewhat dusky.
     * (Unlike absolute light intensity, which scales logarithmic to the eye's perceived brightness)
     *
     * @return the daylight basis points, an integer between 0 (inclusive) and 10000 (inclusive)
     */
    public int getDaylight();

}
