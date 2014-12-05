package com.programyourhome.ir.winlirc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WinLIRCRemote {

    private final String name;
    private final List<String> keys;

    public WinLIRCRemote(final String name) {
        this.name = name;
        this.keys = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getKeys() {
        return this.keys;
    }

    public void addKey(final String key) {
        this.keys.add(key);
    }

    public void addAllKeys(final Collection<String> keys) {
        this.keys.addAll(keys);
    }

}
