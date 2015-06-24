package com.programyourhome.server.events.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LoggerListener implements ApplicationListener<ApplicationEvent> {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        this.log.info("Event caught: " + event);
    }

}
