package com.programyourhome.server.events;

public abstract class ValueChangedTopicEvent<T> extends ValueChangedEvent<T> {

    private static final long serialVersionUID = 1L;

    public ValueChangedTopicEvent(final T oldValue, final T newValue) {
        super(oldValue, newValue);
    }

    @Override
    public boolean hasTopic() {
        return true;
    }

}
