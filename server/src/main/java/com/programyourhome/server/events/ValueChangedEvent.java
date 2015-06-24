package com.programyourhome.server.events;

public abstract class ValueChangedEvent<T> extends PyhTopicEvent {

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

    // The method getTopic should still be overridden by the subclass. This redefinition will force that.
    @Override
    public abstract String getTopic();

    @Override
    // Default implementation for the payload. Can be overridden by subclasses when a more specific payload is required.
    public Object getPayload() {
        return this.createPayload("oldValue", this.oldValue, "newValue", this.newValue);
    }
}
