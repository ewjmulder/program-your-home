package com.programyourhome.environment.mock;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.common.serialize.SerializationSettings;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.voice.model.PyhLanguage;

@Component
public class MockingConfiguration {

    @Inject
    private SerializationSettings serializationSettings;

    @PostConstruct
    public void provideSerializationSettings() {
        this.serializationSettings.fixSerializationScope(PyhLight.class, PyhDevice.class, PyhLanguage.class);
    }

}
