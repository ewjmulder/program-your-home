package com.programyourhome.server.activities;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class TaskSchedulerAndExecutorBean extends ThreadPoolTaskScheduler {

    private static final long serialVersionUID = 1L;

    private void init() {
        // TODO: dynamic configuration and sane values?
        this.getScheduledThreadPoolExecutor().setCorePoolSize(10);
        this.getScheduledThreadPoolExecutor().setMaximumPoolSize(100);
        this.setAwaitTerminationSeconds(5);
        // TODO: proper error handling. Is this scheduled task now dead?
        this.setErrorHandler(Throwable::printStackTrace);
    }

    @Override
    // So we can force our own init to run after the super class initialize.
    public void initialize() {
        super.initialize();
        this.init();
    }
}
