package com.programyourhome.hue;

public interface Light {

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

}
