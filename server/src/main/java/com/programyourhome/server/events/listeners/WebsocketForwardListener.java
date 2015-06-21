package com.programyourhome.server.events.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.programyourhome.server.events.PyhEvent;

@Component
public class WebsocketForwardListener implements ApplicationListener<PyhEvent> {

    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(final PyhEvent event) {
        if (event.hasTopic()) {
            this.template.convertAndSend(event.getTopic(), event.getPayload());
            this.log.debug("Event on topic: '" + event.getTopic() + "' with payload: '" + event.getPayload() + "' forwarded to websocket.");
        }
    }

}
