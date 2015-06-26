package com.programyourhome.ir.model;

import java.util.List;

public interface PyhDevice {

    public int getId();

    public String getName();

    public String getDescription();

    public boolean isPowerDevice();

    public boolean isInputDevice();

    public boolean isVolumeDevice();

    public boolean isChannelDevice();

    public boolean isPlayDevice();

    public boolean isSkipDevice();

    public boolean isRecordDevice();

    public boolean isMenuDevice();

    public List<String> getInputs();

    public List<String> getExtraKeys();

    // TODO: add info about current known (cached) state: at least power on/off, maybe even more like input, volume or channel
    // Also add methods to resync cached state if out of sync, e.g. by using another (physical) remote or failed IR command, etc.
    // Probably easiest way to sync (power state) is when off (hmm, if only power, then why off, doesn't matter actually)

    public boolean isOn();

}
