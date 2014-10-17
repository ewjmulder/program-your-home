package com.programyourhome.hue.model;

public class PlugImpl implements Plug {

    private final String name;

    public PlugImpl(final Light light) {
        this.name = light.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

}
