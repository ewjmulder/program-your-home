package com.programyourhome.server.events;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;

//@Component
public class PollerCaller {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("PyhExecutor")
    private TaskScheduler pollerScheduler;

    private final Random random;

    public PollerCaller() {
        this.random = new Random();
    }

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
