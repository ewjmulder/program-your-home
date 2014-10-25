package com.programyourhome.ir.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoteImpl implements Remote {

    private final String name;
    private final String device;
    private final List<String> keys;

    public RemoteImpl(final String name, final String device) {
        this.name = name;
        this.device = device;
        this.keys = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDevice() {
        return this.device;
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
