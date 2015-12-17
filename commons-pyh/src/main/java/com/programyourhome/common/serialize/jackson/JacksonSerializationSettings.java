package com.programyourhome.common.serialize.jackson;

import java.util.Arrays;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.programyourhome.common.serialize.SerializationSettings;

@Component
public class JacksonSerializationSettings implements SerializationSettings {

    @Inject
    public ObjectMapper objectMapper;

    @Inject
    private DynamicJsonSerializeMixinGenerator classGenerator;

    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
    }

    @Override
    public void fixSerializationScope(final Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> this.objectMapper.addMixInAnnotations(clazz, this.classGenerator.generateClass(clazz)));
    }

    @Override
    public void fixSerializationScopeTo(final Class<?> fromClass, final Class<?> toClass) {
        this.objectMapper.addMixInAnnotations(fromClass, this.classGenerator.generateClass(toClass));
    }

}
