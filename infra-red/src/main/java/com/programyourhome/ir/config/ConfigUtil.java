package com.programyourhome.ir.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigUtil {

    public static Collection<Key> extractAllKeys(final Device device) {
        final Set<Key> allKeys = new HashSet<Key>();
        allKeys.addAll(device.getRemote().getKeyMapping().getKeyGroups().stream()
                // TODO: remove this Eclipse compiler hint when on Eclipse Mars.
                .<Key> flatMap(keyGroup -> keyGroup.getKeys().stream().map(key -> extractKey(keyGroup, key)))
                .collect(Collectors.toSet()));
        allKeys.addAll(device.getRemote().getKeyMapping().getKeys().stream()
                .map(key -> setDelay(device, key))
                .collect(Collectors.toSet()));
        return allKeys;
    }

    private static Key extractKey(final KeyGroup keyGroup, final Key key) {
        final Key fullKey = copyKey(key);
        if (key.getDelay() == null) {
            // TODO: do we allow to override group delay? -> enforce!
            fullKey.setDelay(keyGroup.getGroupDelay());
        }
        // TODO: do we not allow to override group type? -> enforce!
        fullKey.setType(keyGroup.getGroupType());
        return fullKey;
    }

    private static Key setDelay(final Device device, final Key key) {
        final Key fullKey = copyKey(key);
        if (key.getDelay() == null) {
            fullKey.setDelay(device.getDefaultDelay());
        }
        return fullKey;
    }

    // TODO: This is now done a lot of times during running. Why not do it once in the original config keys or cache?
    private static Key copyKey(final Key key) {
        final Key copy = new Key();
        copy.setId(key.getId());
        copy.setName(key.getName());
        copy.setWinlircName(key.getWinlircName());
        copy.setDelay(key.getDelay());
        copy.setType(key.getType());
        return copy;
    }

}
