package com.programyourhome.environment.mock;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.ir.model.PyhDevice;

@Configuration
public class MockingConfiguration {

    // TODO: Get the list of types to create a mixin for dynamically?
    private static final List<Class<?>> CLASSES = Arrays.asList(PyhDevice.class, PyhLight.class);

    @Autowired
    private DynamicJsonSerializeMixinGenerator classGenerator;

    @Bean
    @Primary
    public ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        for (final Class<?> clazz : CLASSES) {
            final Class<?> generatedClass = this.classGenerator.generateClass(clazz);
            objectMapper.addMixInAnnotations(clazz, generatedClass);
        }
        return objectMapper;
    }

}
