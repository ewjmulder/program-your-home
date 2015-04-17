package com.programyourhome.hue.model;

public interface PyhLight {

    /**
     * The id of the light.
     *
     * @return the id of the light
     */
    public int getId();

    /**
     * The name of the light. Must be unique.
     *
     * @return the name of the light
     */
    public String getName();

    /**
     * The type of the light, e.g. full color hue, lux, plug.
     *
     * @return the type of the light
     */
    public LightType getType();

    public boolean isOn();

}
