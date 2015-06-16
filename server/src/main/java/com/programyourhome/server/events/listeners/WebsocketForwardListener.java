package com.programyourhome.server.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.programyourhome.server.events.sundegree.SunDegreeValueChangedEvent;

@Component
public class WebsocketForwardListener implements ApplicationListener<SunDegreeValueChangedEvent> {

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(final SunDegreeValueChangedEvent event) {
        this.template.convertAndSend("/topic/event/sunDegree", event.getNewValue());
    }

}
