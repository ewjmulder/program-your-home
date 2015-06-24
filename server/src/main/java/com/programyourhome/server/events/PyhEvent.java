package com.programyourhome.server.events;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

//TODO: Document: subclasses should have (at least some) fields that define their identity, because of use of hashcode and equals builder
public abstract class PyhEvent extends ApplicationEvent {

    private static final String[] REFLECTION_BUILDER_EXCLUDED_FIELDS = new String[] { "timestamp", "source" };

    private static final long serialVersionUID = 1L;

    private enum PyhEventSource {
        INSTANCE;
    }

    public PyhEvent() {
        // No need to save the source, we'll just use this enum constant as a dummy source.
        super(PyhEventSource.INSTANCE);
    }

    public abstract boolean hasTopic();

    // Override when hasTopic() is set to return true.
    public String getTopic() {
        return null;
    }

    // Override when hasTopic() is set to return true.
    public Object getPayload() {
        return null;
    }

    protected Map<String, Object> createPayload(final Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Create payload must be called with an even number of parameters.");
        }
        final Map<String, Object> payload = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            final Object key = keyValuePairs[i];
            final Object value = keyValuePairs[i + 1];
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("A key of a payload property must be a String, not a '" + key.getClass().getName() + "'.");
            }
            payload.put((String) key, value);
        }
        return payload;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, REFLECTION_BUILDER_EXCLUDED_FIELDS);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, REFLECTION_BUILDER_EXCLUDED_FIELDS);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
