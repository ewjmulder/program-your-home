package com.programyourhome.hue;

import java.awt.Color;
import java.util.Collection;

public interface PhilipsHue {

    /**
     * Returns all lights that are connected to the Hue Bridge.
     *
     * @return all lights
     */
    public Collection<Light> getLights();

    /**
     * Returns all plugs that are connected to the Hue Bridge without involving a light.
     *
     * @return all non-light plugs
     */
    public Collection<Plug> getPlugs();

    /**
     * Turn on the light with this name.
     * The rest of the light properties will be
     * the same as the last time it was on or have a default value.
     *
     * @param lightName the name of the light
     */
    public void turnOnLight(String lightName);

    /**
     * Turn off the light with this name.
     *
     * @param lightName the name of the light
     */
    public void turnOffLight(String lightName);

    /**
     * Turn on the plug with this name.
     *
     * @param lightName the name of the light
     */
    public void turnOnPlug(String plugName);

    /**
     * Turn off the plug with this name.
     *
     * @param lightName the name of the light
     */
    public void turnOffPlug(String plugName);

    /**
     * Dimm the light with this name to the indicated fraction (0,1].
     * Dimming to 0 is not possible, since this would mean turning the light off.
     * Dimming to 1 means setting the light to it's maximum capacity.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightName the name of the light
     * @param fraction the dimming fraction, a decimal number between 0 (exclusive) and 1 (inclusive).
     */
    public void dimmLight(String lightName, double fraction);

    /**
     * Set the color of this light to the indicated color.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightName the name of the light
     * @param color the color
     */
    public void setColor(String lightName, Color color);

    /**
     * Set the mood of this light to the indicated mood.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightName the name of the light
     * @param mood the mood
     */
    public void setMood(String lightName, Mood mood);

    /**
     * Combination of dimming the light and setting it's color.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dimmLight
     * @see setColor
     */
    public void setLight(String lightName, double dimmFraction, Color color);

    /**
     * Combination of dimming the light and setting it's mood.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dimmLight
     * @see setColor
     */
    public void setLight(String lightName, double dimmFraction, Mood mood);

}
