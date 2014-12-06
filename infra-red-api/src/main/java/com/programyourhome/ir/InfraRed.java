package com.programyourhome.ir;

import java.util.Collection;

import com.programyourhome.ir.model.PyhDevice;

public interface InfraRed {

    public Collection<PyhDevice> getDevices();

    // TODO: have the possibility to get a device type, e.g.: getVolumeDevice(int deviceId) ??

    // TODO: Add extra device types: play/pause/forward thingy and menu-thingy (menu/back/enter/arrows)

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

    public void pressKeyOnRemote(int deviceId, int keyId);

}
