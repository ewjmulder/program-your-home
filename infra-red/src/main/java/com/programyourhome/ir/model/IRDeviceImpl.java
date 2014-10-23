package com.programyourhome.ir.model;

public class IRDeviceImpl implements IRDevice {

    private final String name;

    public IRDeviceImpl(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
