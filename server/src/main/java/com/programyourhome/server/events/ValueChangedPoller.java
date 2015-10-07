package com.programyourhome.server.events;

import java.lang.reflect.Constructor;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Generic poller logic for value changed events.
 *
 * @param <T> The type of the value that we poll for. Must have a working equals method!
 */
public abstract class ValueChangedPoller<T> implements Poller {

    private final Log log = LogFactory.getLog(this.getClass());

    @Inject
    private ApplicationEventPublisher eventPublisher;

    private final Class<T> valueClass;
    private final Class<? extends ValueChangedEvent<T>> valueChangedEventClass;
    private T lastPolledValue;

    public ValueChangedPoller(final Class<T> valueClass, final Class<? extends ValueChangedEvent<T>> valueChangedEventClass) {
        this.valueClass = valueClass;
        this.valueChangedEventClass = valueChangedEventClass;
        this.lastPolledValue = null;
    }

    public Class<T> getValueClass() {
        return this.valueClass;
    }

    public Class<? extends ValueChangedEvent<T>> getValueChangedEventClass() {
        return this.valueChangedEventClass;
    }

    public T getLastPolledValue() {
        return this.lastPolledValue;
    }

    protected abstract T getCurrentValue();

    @Override
    public void poll() {
        final T currentValue = this.getCurrentValue();
        // Design choice: we don't publish an event if there is no old value yet.
        if (this.lastPolledValue != null && !currentValue.equals(this.lastPolledValue)) {
            try {
                // Get the declared constructor with 2 arguments of type T: the old and the new value.
                final Constructor<? extends ValueChangedEvent<T>> constructor =
                        this.valueChangedEventClass.getDeclaredConstructor(this.valueClass, this.valueClass);
                // Invoke the constructor with the old (last polled) and the new (current) value.
                final ValueChangedEvent<T> event = constructor.newInstance(this.lastPolledValue, currentValue);
                this.eventPublisher.publishEvent(event);
            } catch (final Exception e) {
                this.log.error("Exception while creating value changed event of type: '" + this.valueChangedEventClass.getName() + "'.", e);
            }
        }
        this.lastPolledValue = currentValue;
    }
}
