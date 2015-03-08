package com.programyourhome.sensors;

import java.math.BigDecimal;

public interface SunDegreeSensor {

    /**
     * Gets the degree of the sun in the sky as it currently is. The returned value can be either positive (day) or negative (night)
     *
     * @return the degree of the sun in the sky, within the range [-89.9, 89.9]
     */
    public BigDecimal getSunDegree();

    /**
     * Is now the astronomical sunrise? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the astronomical sunrise is now (true) or not (false)
     */
    public boolean isAstronomicalSunrise(int margin);

    /**
     * Is now the nautical sunrise? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the nautical sunrise is now (true) or not (false)
     */
    public boolean isNauticalSunrise(int margin);

    /**
     * Is now the civil sunrise? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the civil sunrise is now (true) or not (false)
     */
    public boolean isCivilSunrise(int margin);

    /**
     * Is now the official sunrise? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the official sunrise is now (true) or not (false)
     */
    public boolean isOfficialSunrise(int margin);

    /**
     * Is now the official sunset? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the official sunset is now (true) or not (false)
     */
    public boolean isOfficialSunset(int margin);

    /**
     * Is now the civil sunset? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the civil sunset is now (true) or not (false)
     */
    public boolean isCivilSunset(int margin);

    /**
     * Is now the nautical sunset? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the nautical sunset is now (true) or not (false)
     */
    public boolean isNauticalSunset(int margin);

    /**
     * Is now the astronomical sunset? The answer will be based on the calculated time +/- the margin given.
     *
     * @param margin the margin in minutes to take into account, within the range [0, 10]
     * @return the astronomical sunset is now (true) or not (false)
     */
    public boolean isAstronomicalSunset(int margin);

}
