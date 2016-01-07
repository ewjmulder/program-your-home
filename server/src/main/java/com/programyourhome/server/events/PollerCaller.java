package com.programyourhome.server.events;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class PollerCaller {

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    @Qualifier("PyhExecutor")
    private TaskScheduler pollerScheduler;

    private final Random random;

    public PollerCaller() {
        this.random = new Random();
    }

    // TODO: idea: create flag in Poller interface that does:
    // - when a change is detected (boolean return value of poll()
    // - increase the polling frequency by a certain factor (eg 10)
    // - until for the normal polling interval time, no more changes are detected
    // - example use case: keeping track of the mouse that is moving, keeping a light state in sync that is changing
    @PostConstruct
    public void init() {
        final Map<String, Poller> pollers = this.applicationContext.getBeansOfType(Poller.class);
        for (final Poller poller : pollers.values()) {
            this.pollerScheduler.scheduleWithFixedDelay(() -> poller.poll(),
                    DateUtils.addMilliseconds(new Date(), this.generateRandomInitialDelay()), poller.getIntervalInMillis());
        }
    }

    /**
     * Generate a random initial delay, so the pollers won't be fired all at once.
     *
     * @return
     */
    private int generateRandomInitialDelay() {
        return this.random.nextInt(3000) + 1000;
    }
}
