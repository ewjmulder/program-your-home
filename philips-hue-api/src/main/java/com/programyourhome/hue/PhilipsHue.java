package com.programyourhome.hue;

import java.awt.Color;
import java.util.Collection;

import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.hue.model.PyhPlug;

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
    public Collection<PyhLight> getLights();

    /**
     * Returns all plugs that are connected to the Hue Bridge.
     *
     * Note: This also includes all plugs that are connected to a light, since this module
     * has no way of knowing which plug is connected to a light and which plug is connected to another device.
     *
     * @return all non-light plugs
     */
    public Collection<PyhPlug> getPlugs();

    /**
     * Turn on the light with this id.
     * The rest of the light properties will be
     * the same as the last time it was on or have a default value.
     *
     * @param lightId the id of the light
     */
    public void turnOnLight(int lightId);

    /**
     * Turn off the light with this id.
     *
     * @param lightId the id of the light
     */
    public void turnOffLight(int lightId);

    /**
     * Turn on the plug with this id.
     *
     * @param plugId the id of the light
     */
    public void turnOnPlug(int plugId);

    /**
     * Turn off the plug with this id.
     *
     * @param plugId the id of the light
     */
    public void turnOffPlug(int plugId);

    /**
     * Dim this light to the indicated basis points [0, 10000].
     * Dimming to 0 is possible, this is not the same as off.
     * Dimming to 10000 means setting the light to it's maximum capacity.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightId the id of the light
     * @param dimBasisPoints the dimming basis points, an integer between 0 (inclusive) and 10000 (inclusive)
     */
    public void dim(int lightId, int dimBasisPoints);

    /**
     * Set the color of this light to the indicated color, defined with RGB values.
     * Please note that not all lights support all colors. For more information, see setColorXY.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightId the id of the light
     * @param color the color
     */
    public void setColorRGB(int lightId, Color color);

    /**
     * Set the color of this light to the specified point x, y in the CIE 1931 color space
     * (http://en.wikipedia.org/wiki/CIE_1931_color_space).
     * Philips Hue does not support the full spectrum and not every light supports the same portion of the color space.
     * The light will be set to the closest matching color that is supported. Please note that this can
     * For more information, see the following reference material:
     * https://github.com/PhilipsHue/PhilipsHueSDK-iOS-OSX/blob/master/ApplicationDesignNotes/RGB%20to%20xy%20Color%20
     * conversion.md
     * http://www.developers.meethue.com/documentation/core-concepts - Color gets more complicated
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightId the id of the light
     * @param x the x coordinate in the color space
     * @param y the y coordinate in the color space
     */
    public void setColorXY(int lightId, float x, float y);

    /**
     * Set the color of this light to the specified hue and saturation basis points [0, 10000].
     * A hue of 0 translates to an internal value of 0 and 10000 translates to 65535. This in turn is translated
     * to a specific 'base' color with that hue value, see also http://en.wikipedia.org/wiki/Hue.
     * A saturation of 0 means the most 'white' (neutral) color, while 10000 means the most intense, saturated color.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightId the id of the light
     * @param hueBasisPoints the hue basis points, an integer between 0 (inclusive) and 10000 (inclusive)
     * @param saturationBasisPoints the saturation basis points, an integer between 0 (inclusive) and 10000 (inclusive)
     */
    public void setColorHueSaturation(int lightId, int hueBasisPoints, int saturationBasisPoints);

    /**
     * Set the color temperature of this light to the specified basis points [0, 10000] where 0 is the coldest and 10000
     * is the warmest possible color.
     * This will translate (linearly) to an amount of mirek (http://en.wikipedia.org/wiki/Mired) in the range of 153
     * (coolest) to 500 (warmest).
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightId the id of the light
     * @param temperatureBasisPoints the temperature basis points, an integer between 0 (inclusive) and 10000
     *            (inclusive)
     */
    public void setColorTemperature(int lightId, int temperatureBasisPoints);

    /**
     * Set the mood of this light to the indicated mood.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @param lightId the id of the light
     * @param mood the mood
     */
    public void setMood(int lightId, Mood mood);

    /**
     * Combination of dimming the light and setting it's color RGB.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dim
     * @see setColorRGB
     */
    public void dimToColorRGB(int lightId, int dimBasisPoints, Color color);

    /**
     * Combination of dimming the light and setting it's color XY.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dim
     * @see setColorXY
     */
    public void dimToColorXY(int lightId, int dimBasisPoints, float x, float y);

    /**
     * Combination of dimming the light and setting it's color hue saturation.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dim
     * @see setColorHueSaturation
     */
    public void dimToColorHueSaturation(int lightId, int dimBasisPoints, int hueBasisPoints, int saturationBasisPoints);

    /**
     * Combination of dimming the light and setting it's color temperature.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dim
     * @see setColorTemperature
     */
    public void dimToColorTemperature(int lightId, int dimBasisPoints, int temperatureBasisPoints);

    /**
     * Combination of dimming the light and setting it's mood.
     *
     * Note: if this light is currently off, it will be turned on.
     *
     * @see dim
     * @see setMood
     */
    public void dimToMood(int lightId, int dimBasisPoints, Mood mood);

}
