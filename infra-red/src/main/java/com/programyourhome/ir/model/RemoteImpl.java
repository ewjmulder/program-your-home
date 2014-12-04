package com.programyourhome.ir.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoteImpl implements PyhRemote {

    private final int id;
    private final String name;
    private final String deviceName;
    private final List<String> keys;

    public RemoteImpl(final int id, final String name, final String deviceName) {
        this.id = id;
        this.name = name;
        this.deviceName = deviceName;
        this.keys = new ArrayList<>();
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
    public String getDeviceName() {
        return this.deviceName;
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<String>(this.keys);
    }

    public void addKey(final String key) {
        this.keys.add(key);
    }

    public void addAllKeys(final Collection<String> keys) {
        this.keys.addAll(keys);
    }

}
