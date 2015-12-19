package com.programyourhome.common.eventstore;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EventStore {

    private static final String PATH_STREAM = "streams/%s/incoming/%s";
    private static final String PATH_PROJECTION_PARTITION = "projection/%s/state";
    private static final String QUERY_PROJECTION_PARTITION = "partition=%s";
    private static final String EVENT_TYPE_HEADER_NAME = "ES-EventType";

    @Value("${eventstore.host}")
    private String host;

    @Value("${eventstore.port}")
    private int port;

    private final RestTemplate restTemplate;

    public EventStore() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Post an event to the event store.
     * The event object will be converted to JSON using the default Jackson serialization rules.
     *
     * @param stream the stream name
     * @param type the event type name
     * @param event the event object
     */
    public void postEvent(final String stream, final String type, final Object event) {
        final String path = String.format(PATH_STREAM, stream, UUID.randomUUID());
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(this.host)
                .port(this.port)
                .path(path);

        final RequestEntity<Object> requestEntity = RequestEntity
                .post(uriBuilder.build().toUri())
                .header(EVENT_TYPE_HEADER_NAME, type)
                .body(event);
        this.restTemplate.exchange(requestEntity, Void.class);
    }

    /**
     * Get the state of a projection in the event store.
     * The response is expected to match the type given, according to the Jackson default deserialization rules.
     *
     * @param projection the projection name
     * @param responseType the response type
     * @return the response as an object of the given type
     */
    public <T> T getProjectionState(final String projection, final Class<T> responseType) {
        return this.getProjectionState(projection, null);
    }

    /**
     * Get the state of a partition of a projection in the event store.
     * The response is expected to match the type given, according to the Jackson default deserialization rules.
     *
     * @param projection the projection name
     * @param partition the partition name
     * @param responseType the response type
     * @return the response as an object of the given type
     */
    public <T> T getProjectionState(final String projection, final String partition, final Class<T> responseType) {
        final String path = String.format(PATH_PROJECTION_PARTITION, projection);
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(this.host)
                .port(this.port)
                .path(path);
        if (partition != null) {
            uriBuilder.query(String.format(QUERY_PROJECTION_PARTITION, partition));
        }
        return this.restTemplate.getForEntity(uriBuilder.build().toUri(), responseType).getBody();
    }

}
