package com.programyourhome.environment.mock;

import java.util.Arrays;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.LightType;
import com.programyourhome.hue.model.PyhLight;

@Configuration
public class PhilipsHueMock extends PyhMock {

    @Bean
    public PhilipsHue createPhilipsHueMock() {
        final PhilipsHue philipsHueMock = this.createMock(PhilipsHue.class);
        final PyhLight light = Mockito.mock(PyhLight.class);
        Mockito.when(light.getId()).thenReturn(1);
        Mockito.when(light.getName()).thenReturn("Mock name");
        Mockito.when(light.getType()).thenReturn(LightType.LIVING_COLORS);
        Mockito.when(philipsHueMock.getLights()).thenReturn(Arrays.asList(light));
        return philipsHueMock;
    }
}
