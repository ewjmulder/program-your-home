package com.programyourhome.server.events;

import java.util.HashMap;
import java.util.Map;

public abstract class PyhTopicEvent extends PyhEvent {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean hasTopic() {
        return true;
    }

    // The method getTopic should still be overridden by the subclass. This redefinition will force that.
    @Override
    public abstract String getTopic();

    // The method getPayload should still be overridden by the subclass. This redefinition will force that.
    @Override
    public abstract Object getPayload();

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

}
