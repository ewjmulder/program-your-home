package com.programyourhome.server.events.listeners;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EventStoreListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        // TODO: put event in event store db. Base stream name on topic in event, other stream name for non-topic events.
    }

}
