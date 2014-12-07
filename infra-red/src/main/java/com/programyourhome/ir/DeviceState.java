package com.programyourhome.ir;

public class DeviceState {

    private boolean turnedOn;
    private String input;

    public DeviceState() {
        this.turnedOn = false;
        this.input = null;
    }

    public boolean isTurnedOn() {
        return this.turnedOn;
    }

    public boolean isTurnedOff() {
        return !this.turnedOn;
    }

    public String getInput() {
        return this.input;
    }

    public void turnOn() {
        this.turnedOn = true;
    }

    public void turnOff() {
        this.turnedOn = false;
    }

    public void setInput(final String input) {
        this.input = input;
    }

}
