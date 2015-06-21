package com.programyourhome.server.events;

public abstract class ValueChangedEvent<T> extends PyhEvent {

    private static final long serialVersionUID = 1L;

    private final T oldValue;
    private final T newValue;

    public ValueChangedEvent(final T oldValue, final T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public T getOldValue() {
        return this.oldValue;
    }

    public T getNewValue() {
        return this.newValue;
    }

}
