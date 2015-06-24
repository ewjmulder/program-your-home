package com.programyourhome.hue.model;

import com.programyourhome.common.model.PyhImpl;

public class PlugImpl extends PyhImpl implements PyhPlug {

    private final String name;

    public PlugImpl(final PyhLight light) {
        this.name = light.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

}
