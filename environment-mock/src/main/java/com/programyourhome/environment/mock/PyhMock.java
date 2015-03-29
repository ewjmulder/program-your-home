package com.programyourhome.environment.mock;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.mockito.listeners.InvocationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.ir.model.PyhDevice;

public abstract class PyhMock {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private DynamicJsonSerializeMixinGenerator classGenerator;

    private final InvocationListener invocationLogger;

    public PyhMock() {
        this.invocationLogger = methodInvocationReport -> this.log.debug("Mock method invocation: " + methodInvocationReport);
    }

    // TODO: This bean will be generated for every subclass, right? Find a workaround!
    @Bean
    @Primary
    public ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Class<?> clazz = this.classGenerator.generateClass(PyhDevice.class);
        // TODO: Get the list of types to create a mixin for dynamically.
        objectMapper.addMixInAnnotations(PyhDevice.class, clazz);
        final Class<?> clazz2 = this.classGenerator.generateClass(PyhLight.class);
        objectMapper.addMixInAnnotations(PyhLight.class, clazz2);
        return objectMapper;
    }

    @PostConstruct
    public void init() {
        this.log.info(this.getClass().getSimpleName() + " initialized");
    }

    protected <T> T createMock(final Class<T> clazz) {
        return Mockito.mock(clazz, Mockito.withSettings().invocationListeners(this.invocationLogger));
    }

}
