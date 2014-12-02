package com.programyourhome.server.activities;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class TaskExecutorBean extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = 1L;

    // TODO: dynamic configuration and sane values?
    public TaskExecutorBean() {
        this.setCorePoolSize(10);
        this.setMaxPoolSize(100);
        this.setAwaitTerminationSeconds(5);
    }

}
