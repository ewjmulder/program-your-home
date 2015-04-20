package com.programyourhome.ir;

import java.util.Collection;

import com.programyourhome.ir.model.PyhDevice;

public interface InfraRed {

    public Collection<PyhDevice> getDevices();

    public PyhDevice getDevice(int deviceId);

    public void turnOn(int deviceId);

    public void turnOff(int deviceId);

    // TODO: use id's for inputs instead of string based names?
    public void setInput(int deviceId, String input);

    public void volumeUp(int deviceId);

    public void volumeDown(int deviceId);

    public void volumeMute(int deviceId);

    public void channelUp(int deviceId);

    public void channelDown(int deviceId);

    public void setChannel(int deviceId, int channel);

    public void play(int deviceId);

    public void pause(int deviceId);

    public void stop(int deviceId);

    public void fastForward(int deviceId);

    public void rewind(int deviceId);

    public void skipNext(int deviceId);

    public void skipPrevious(int deviceId);

    public void record(int deviceId);

    public void menuToggle(int deviceId);

    public void menuSelect(int deviceId);

    public void menuBack(int deviceId);

    public void menuUp(int deviceId);

    public void menuDown(int deviceId);

    public void menuLeft(int deviceId);

    public void menuRight(int deviceId);

    public void pressKeyOnRemote(int deviceId, int keyId);

}
