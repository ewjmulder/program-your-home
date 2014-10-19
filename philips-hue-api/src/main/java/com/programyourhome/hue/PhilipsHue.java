package com.programyourhome.hue;

import java.awt.Color;
import java.util.Collection;

import com.programyourhome.hue.model.Light;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.Plug;

public interface PhilipsHue {

    /**
     * Returns whether this module is currently connected to the Hue Bridge.
     *
     * @return connected (true) or not (false)
     */
    public boolean isConnectedToBridge();

    /**
     * Returns all lights that are connected to the Hue Bridge.
     *
     * Note: This also includes all plugs (identified as LightType LIVING_WHITES_PLUG), since this module
     * has no way of knowing which plug is connected to a light and which plug is connected to another device.
     *
     * @return all lights
     */
    public Collection<Light> getLights();

    /**
     * Returns all plugs that are connected to the Hue Bridge.
     *
     * Note: This also includes all plugs that are connected to a light, since this module
     * has no way of knowing which plug is connected to a light and which plug is connected to another device.
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
     * Dim the light with this name to the indicated fraction [0,1].
     * Dimming to 0 is possible, this is not the same as off.
     * Dimming to 1 means setting the light to it's maximum capacity.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightName the name of the light
     * @param fraction the dimming fraction, a decimal number between 0 (inclusive) and 1 (inclusive).
     */
    public void dimLight(String lightName, double fraction);

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
     * Set the color temperature to the specified mirek value [153, 500].
     *
     * @param lightName the name of the light
     * @param mirek the amount of mirek (http://en.wikipedia.org/wiki/Mired) in the range of 153 (coolest) to 500 (warmest)
     */
    public void setColorTemperature(String lightName, int mirek);

    /**
     * Combination of dimming the light and setting it's color.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dimmLight
     * @see setColor
     */
    public void dimToColor(String lightName, double dimFraction, Color color);

    /**
     * Combination of dimming the light and setting it's mood.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dimmLight
     * @see setColor
     */
    public void dimToMood(String lightName, double dimFraction, Mood mood);

}
