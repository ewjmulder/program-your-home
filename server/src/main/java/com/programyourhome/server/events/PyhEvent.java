package com.programyourhome.server.events;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.ApplicationEvent;

//TODO: Document: subclasses should have (at least some) fields that define their identity, because of use of hashcode and equals builder
public class PyhEvent extends ApplicationEvent {

    private static final String[] REFLECTION_BUILDER_EXCLUDED_FIELDS = new String[] { "timestamp", "source" };

    private static final long serialVersionUID = 1L;

    private enum PyhEventSource {
        INSTANCE;
    }

    public PyhEvent() {
        // No need to save the source, we'll just use this enum constant as a dummy source.
        super(PyhEventSource.INSTANCE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, REFLECTION_BUILDER_EXCLUDED_FIELDS);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, REFLECTION_BUILDER_EXCLUDED_FIELDS);
    }

}
