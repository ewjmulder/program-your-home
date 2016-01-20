package com.programyourhome.ir.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.ir.config.Device;
import com.programyourhome.ir.config.Key;
import com.programyourhome.ir.config.KeyType;

public class PyhDeviceImpl extends PyhImpl implements PyhDevice {

    private final int id;
    private final String name;
    private final String description;
    private final boolean isPowerDevice;
    private final boolean isInputDevice;
    private final boolean isVolumeDevice;
    private final boolean isChannelDevice;
    private final boolean isPlayDevice;
    private final boolean isSkipDevice;
    private final boolean isRecordDevice;
    private final boolean isMenuDevice;
    private final List<String> inputs;
    private final List<String> extraKeys;

    private final boolean on;

    public PyhDeviceImpl(final Device device, final boolean on) {
        this.id = device.getId();
        this.name = device.getName();
        this.description = device.getDescription();
        this.isPowerDevice = device.getPrototypes().isPower();
        this.isInputDevice = device.getPrototypes().isInput();
        this.isVolumeDevice = device.getPrototypes().isVolume();
        this.isChannelDevice = device.getPrototypes().isChannel();
        this.isPlayDevice = device.getPrototypes().isPlay();
        this.isSkipDevice = device.getPrototypes().isSkip();
        this.isRecordDevice = device.getPrototypes().isRecord();
        this.isMenuDevice = device.getPrototypes().isMenu();

        // TODO: Generify these two, possibly use some (static) util where this key getting pattern is placed.
        this.inputs = device.getRemote().getKeyMapping().getKeyGroups().stream()
                .filter(keyGroup -> keyGroup.getGroupType() == KeyType.INPUT)
                .flatMap(keyGroup -> keyGroup.getKeys().stream())
                .map(Key::getName)
                .collect(Collectors.toList());
        this.inputs.addAll(device.getRemote().getKeyMapping().getKeys().stream()
                .filter(key -> key.getType() == KeyType.INPUT)
                .map(Key::getName)
                .collect(Collectors.toList()));

        this.extraKeys = device.getRemote().getKeyMapping().getKeyGroups().stream()
                .filter(keyGroup -> keyGroup.getGroupType() == null)
                .flatMap(keyGroup -> keyGroup.getKeys().stream())
                .map(Key::getName)
                .collect(Collectors.toList());
        this.extraKeys.addAll(device.getRemote().getKeyMapping().getKeys().stream()
                .filter(key -> key.getType() == null)
                .map(Key::getName)
                .collect(Collectors.toList()));

        this.on = on;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean isPowerDevice() {
        return this.isPowerDevice;
    }

    @Override
    public boolean isInputDevice() {
        return this.isInputDevice;
    }

    @Override
    public boolean isVolumeDevice() {
        return this.isVolumeDevice;
    }

    @Override
    public boolean isChannelDevice() {
        return this.isChannelDevice;
    }

    @Override
    public boolean isPlayDevice() {
        return this.isPlayDevice;
    }

    @Override
    public boolean isSkipDevice() {
        return this.isSkipDevice;
    }

    @Override
    public boolean isRecordDevice() {
        return this.isRecordDevice;
    }

    @Override
    public boolean isMenuDevice() {
        return this.isMenuDevice;
    }

    @Override
    public List<String> getInputs() {
        return new ArrayList<>(this.inputs);
    }

    @Override
    public List<String> getExtraKeys() {
        return new ArrayList<>(this.extraKeys);
    }

    @Override
    public boolean isOn() {
        return this.on;
    }

}
