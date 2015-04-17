package com.programyourhome.hue.model;

import com.philips.lighting.model.PHLight;

public class PyhLightImpl implements PyhLight {

    private final int id;
    private final String name;
    private final LightType type;
    private final boolean on;

    // TODO: make a copy / clone of the phLight / last known light state and serve the PyhLight props from there?
    public PyhLightImpl(final PHLight phLight) {
        this.id = Integer.parseInt(phLight.getIdentifier());
        this.name = phLight.getName();
        this.type = LightType.fromModelAbbreviation(phLight.getModelNumber().substring(0, 3));
        this.on = phLight.getLastKnownLightState().isOn();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LightType getType() {
        return this.type;
    }

    @Override
    public boolean isOn() {
        return this.on;
    }

    // TODO: add all color, dim, etc properties from last known light state
    // calculate RGB color value: PHUtilities.colorFromXY
    // Do so according to 'own' chosen domain, like expressed in the API URL's and params, esp:
    // - param names and ranges should match

}
