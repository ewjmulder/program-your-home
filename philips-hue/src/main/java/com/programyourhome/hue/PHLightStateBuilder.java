package com.programyourhome.hue;

import java.awt.Color;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightColorMode;
import com.philips.lighting.model.PHLightState;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.util.ValueUtil;

/**
 * TODO: Javadoc and explain why to use this and never to use an object gotten from the cache directly! and nore this will always turn the light on.
 */
public class PHLightStateBuilder {

    // TODO: Build in safety features that will prevent you from setting the color several times?
    // TODO: Build in safety features that will prevent you from calling the builder after build()?
    // TODO: besides the absolute methods, add relative methods like 'soften', 'dim', etc.

    private final PHLight phLight;
    private final PHLightState phLightState;

    public PHLightStateBuilder(final PHLight phLight) {
        this(phLight, true);
    }

    public PHLightStateBuilder(final PHLight phLight, final boolean on) {
        this.phLight = phLight;
        this.phLightState = new PHLightState();
        this.ensureLightOnState(phLight.getLastKnownLightState(), on);
    }

    /**
     * Ensure the light will be in the desired state by setting it to on if it is currently off and vice versa.
     * So we do not set the 'on' parameter if the light is already in the desired state, for performance reasons.
     * (http://www.developers.meethue.com/faq-page - Why is the bridge response so slow?)
     */
    private void ensureLightOnState(final PHLightState lastKnownLightState, final boolean on) {
        if (lastKnownLightState.isOn() != on) {
            this.phLightState.setOn(on);
        }
    }

    public PHLight getPHLight() {
        return this.phLight;
    }

    public PHLightStateBuilder dim(final double dimFraction) {
        return this.dim(ValueUtil.fractionToBrightness(dimFraction));
    }

    public PHLightStateBuilder dim(final int brightness) {
        this.phLightState.setBrightness(brightness);
        return this;
    }

    public PHLightStateBuilder colorRGB(final Color color) {
        // TODO: keep here or move to utility class?
        final float xy[] = PHUtilities.calculateXYFromRGB(color.getRed(), color.getGreen(), color.getBlue(), this.phLight.getModelNumber());
        return this.colorXY(xy[0], xy[1]);
    }

    public PHLightStateBuilder colorXY(final float x, final float y) {
        this.phLightState.setColorMode(PHLightColorMode.COLORMODE_XY);
        this.phLightState.setX(x);
        this.phLightState.setY(y);
        return this;
    }

    public PHLightStateBuilder colorHueSaturation(final double hueFraction, final double saturationFraction) {
        return this.colorHueSaturation(ValueUtil.fractionToHue(hueFraction), ValueUtil.fractionToSaturation(saturationFraction));
    }

    public PHLightStateBuilder colorHueSaturation(final int hue, final int saturation) {
        this.phLightState.setColorMode(PHLightColorMode.COLORMODE_HUE_SATURATION);
        this.phLightState.setHue(hue);
        this.phLightState.setSaturation(saturation);
        return this;
    }

    public PHLightStateBuilder colorTemperature(final double temperatureFraction) {
        return this.colorTemperature(ValueUtil.fractionToColorTemperature(temperatureFraction));
    }

    public PHLightStateBuilder colorTemperature(final int temperature) {
        this.phLightState.setColorMode(PHLightColorMode.COLORMODE_CT);
        this.phLightState.setCt(temperature);
        return this;
    }

    public PHLightStateBuilder mood(final Mood mood) {
        return this.colorTemperature(mood.getTemperature());
    }

    /**
     * Return the actual light state object after it was build with the other methods.
     * After calling build(), the builder object should not be used any further.
     *
     * @return the light state object that was built
     */
    public PHLightState build() {
        // Strip the color properties based on the latest known state of the light.
        this.stripNonColorModeSettings();
        return this.phLightState;
    }

    /**
     * Set the color properties to null that are not relevant for the used light state.
     */
    // TODO: This can be removed if chosen to check that setting a color is called only once on a builder.
    private void stripNonColorModeSettings() {
        if (this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_CT
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_HUE_SATURATION
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_NONE
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_UNKNOWN) {
            this.phLightState.setX(null);
            this.phLightState.setY(null);
        }
        if (this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_XY
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_HUE_SATURATION
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_NONE
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_UNKNOWN) {
            this.phLightState.setCt(null);
        }
        if (this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_XY
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_CT
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_NONE
                || this.phLightState.getColorMode() == PHLightColorMode.COLORMODE_UNKNOWN) {
            this.phLightState.setHue(null);
            this.phLightState.setSaturation(null);
        }
    }

}
