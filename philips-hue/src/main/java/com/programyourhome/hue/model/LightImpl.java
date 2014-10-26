package com.programyourhome.hue.model;

import com.philips.lighting.model.PHLight;

public class LightImpl implements Light {

    private final String name;
    private final LightType type;

    public LightImpl(final PHLight phLight) {
        this.name = phLight.getName();
        this.type = LightType.fromModelAbbreviation(phLight.getModelNumber().substring(0, 3));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LightType getType() {
        return this.type;
    }

    // TODO: add all color, dim, etc properties from last known light state
    // calculate RGB color value: PHUtilities.colorFromXY
    // Do so according to 'own' chosen domain, like expressed in the API URL's and params, esp:
    // - param names and ranges should match

}
