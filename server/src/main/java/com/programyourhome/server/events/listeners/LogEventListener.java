package com.programyourhome.server.events.listeners;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LogEventListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        // TODO: proper logging framework.
        System.out.println("Event caught: " + event);
    }

}
