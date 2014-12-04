package com.programyourhome.hue.model;

public class PlugImpl implements PyhPlug {

    private final String name;

    public PlugImpl(final PyhLight light) {
        this.name = light.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

}
