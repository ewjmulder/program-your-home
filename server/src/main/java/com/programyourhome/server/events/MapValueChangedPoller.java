package com.programyourhome.server.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Generic poller logic for value changed events in a collection of values.
 * It will publish a separate event for every individual change in the collection.
 *
 * @param <Key> The type of the key to use for the mapped items. Must have a unique value over all items in the collection.
 * @param <T> The type of the items values that we poll for. Must have a working equals method!
 */
public abstract class MapValueChangedPoller<Key, T> implements Poller {

    @Inject
    private AutowireCapableBeanFactory factory;

    private final Class<T> valueClass;
    private final Class<? extends ValueChangedEvent<T>> valueChangedEventClass;
    private final Map<Key, T> lastPolledValues;
    private final Map<Key, ValueChangedPoller<T>> itemPollers;

    public MapValueChangedPoller(final Class<T> valueClass, final Class<? extends ValueChangedEvent<T>> valueChangedEventClass) {
        this.valueClass = valueClass;
        this.valueChangedEventClass = valueChangedEventClass;
        this.lastPolledValues = new HashMap<>();
        this.itemPollers = new HashMap<>();
    }

    protected abstract Collection<T> getCurrentCollection();

    protected abstract Key getKey(T item);

    @Override
    public synchronized void poll() {
        final Collection<T> currentCollection = this.getCurrentCollection();
        for (final T currentItem : currentCollection) {
            // Let the subclass compute the key object for this item.
            final Key key = this.getKey(currentItem);
            // Save the key->value pair in the map.
            this.lastPolledValues.put(key, currentItem);
            // Get (or create if none exists yet) the item poller for this key.
            final ValueChangedPoller<T> itemPoller = this.itemPollers.computeIfAbsent(key, aKey -> this.createPoller(aKey));
            // Let the item poller do the actual comparison and possible event publishing.
            itemPoller.poll();
        }
    }

    public ItemValueChangedPoller createPoller(final Key key) {
        final ItemValueChangedPoller poller = new ItemValueChangedPoller(key);
        // Autowire the eventPublisher in the superclass.
        this.factory.autowireBeanProperties(poller, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        return poller;
    }

    // Inner class that connects an item in the collection to the actual value changed poller logic.
    private class ItemValueChangedPoller extends ValueChangedPoller<T> {
        private final Key key;

        public ItemValueChangedPoller(final Key key) {
            super(MapValueChangedPoller.this.valueClass, MapValueChangedPoller.this.valueChangedEventClass);
            this.key = key;
        }

        @Override
        protected T getCurrentValue() {
            return MapValueChangedPoller.this.lastPolledValues.get(this.key);
        }

        @Override
        // Will never be used, because we're not actually using this class as a poller.
        public long getIntervalInMillis() {
            return Long.MAX_VALUE;
        }
    }
}
