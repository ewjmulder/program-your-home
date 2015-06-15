package com.programyourhome.server.events.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.programyourhome.server.events.sundegree.SunDegreeValueChangedEvent;

@Component
public class WebsocketForwardListener implements ApplicationListener<SunDegreeValueChangedEvent> {

    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public void onApplicationEvent(final SunDegreeValueChangedEvent event) {
    }

}
