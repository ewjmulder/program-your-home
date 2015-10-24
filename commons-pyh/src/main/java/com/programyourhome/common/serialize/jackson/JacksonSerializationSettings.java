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
    public void unfixSerializationScope(final Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> this.objectMapper.addMixInAnnotations(clazz, null));
        // TODO: clear cache entry for class?
        // this.objectMapper.getSerializerProvider().getConfig()....
        // TODO: this with view thingy seems more like it!
        // this.objectMapper.getSerializationConfig().withView(view)
    }

}
