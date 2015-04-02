package com.programyourhome.environment.mock;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.mockito.listeners.InvocationListener;

public abstract class PyhMock {

    protected final Log log = LogFactory.getLog(this.getClass());

    private final InvocationListener invocationLogger;

    public PyhMock() {
        this.invocationLogger = methodInvocationReport -> this.log.debug("Mock method invocation: " + methodInvocationReport);
    }

    @PostConstruct
    public void init() {
        this.log.info(this.getClass().getSimpleName() + " initialized");
    }

    protected <T> T createMock(final Class<T> clazz) {
        return Mockito.mock(clazz, Mockito.withSettings().invocationListeners(this.invocationLogger));
    }

}
