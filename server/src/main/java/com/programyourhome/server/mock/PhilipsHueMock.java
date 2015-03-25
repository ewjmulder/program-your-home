package com.programyourhome.server.mock;

import java.awt.Color;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.hue.model.PyhPlug;

@Component
@ConditionalOnProperty("environment.mock")
public class PhilipsHueMock implements PhilipsHue {

    private final Log log = LogFactory.getLog(this.getClass());

    // TODO: Log method calls.
    // TODO: provide simple mock answers to basic method calls.

    @Override
    public boolean isConnectedToBridge() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<PyhLight> getLights() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<PyhPlug> getPlugs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void turnOnLight(final int lightId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOffLight(final int lightId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOnPlug(final int plugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOffPlug(final int plugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dim(final int lightId, final int dimBasisPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorRGB(final int lightId, final Color color) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorXY(final int lightId, final float x, final float y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorHueSaturation(final int lightId, final int hueBasisPoints, final int saturationBasisPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorTemperature(final int lightId, final int temperatureBasisPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMood(final int lightId, final Mood mood) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dimToColorRGB(final int lightId, final int dimBasisPoints, final Color color) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dimToColorXY(final int lightId, final int dimBasisPoints, final float x, final float y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dimToColorHueSaturation(final int lightId, final int dimBasisPoints, final int hueBasisPoints, final int saturationBasisPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dimToColorTemperature(final int lightId, final int dimBasisPoints, final int temperatureBasisPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dimToMood(final int lightId, final int dimBasisPoints, final Mood mood) {
        // TODO Auto-generated method stub

    }

}
