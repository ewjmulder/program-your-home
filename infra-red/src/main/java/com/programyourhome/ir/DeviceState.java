package com.programyourhome.ir;

public class DeviceState {

    private boolean on;
    private String input;

    public DeviceState() {
        this.on = false;
        this.input = null;
    }

    public boolean isOn() {
        return this.on;
    }

    public boolean isOff() {
        return !this.on;
    }

    public String getInput() {
        return this.input;
    }

    public void turnOn() {
        this.on = true;
    }

    public void turnOff() {
        this.on = false;
    }

    public void setInput(final String input) {
        this.input = input;
    }

}
