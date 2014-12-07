package com.programyourhome.ir.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigUtil {

    public static Collection<Key> extractAllKeys(final Device device) {
        final Set<Key> allKeys = new HashSet<Key>();
        allKeys.addAll(device.getRemote().getKeyMapping().getKeyGroups().stream()
                .flatMap(keyGroup -> keyGroup.getKeys().stream().map(key -> extractKey(keyGroup, key)))
                .collect(Collectors.toSet()));
        allKeys.addAll(device.getRemote().getKeyMapping().getKeys());
        return allKeys;
    }

    private static Key extractKey(final KeyGroup keyGroup, final Key key) {
        final Key fullKey = new Key();
        fullKey.setId(key.getId());
        fullKey.setName(key.getName());
        fullKey.setWinlircName(key.getWinlircName());
        fullKey.setDelay(keyGroup.getGroupDelay());
        fullKey.setType(keyGroup.getGroupType());
        return fullKey;
    }

}
