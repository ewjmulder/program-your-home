package com.programyourhome.environment.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.LightType;
import com.programyourhome.hue.model.PyhLight;

@Configuration
public class PhilipsHueMock extends PyhMock {

    private final Map<Integer, Boolean> lightStates;

    public PhilipsHueMock() {
        this.lightStates = new HashMap<>();
        this.lightStates.put(1, false);
    }

    @Bean
    public PhilipsHue createPhilipsHueMock() {
        final PhilipsHue philipsHueMock = this.createMock(PhilipsHue.class);

        // TODO: abstract out mock creation code.
        // Mock light(s)
        final PyhLight light = Mockito.mock(PyhLight.class);
        Mockito.when(light.getId()).thenReturn(1);
        Mockito.when(light.getName()).thenReturn("Mock name");
        Mockito.when(light.getType()).thenReturn(LightType.HUE_FULL_COLOR_BULB);
        Mockito.when(light.isOn()).thenAnswer(invocation -> this.lightStates.get(1));

        final PyhLight light2 = Mockito.mock(PyhLight.class);
        Mockito.when(light2.getId()).thenReturn(2);
        Mockito.when(light2.getName()).thenReturn("Mock name 2");
        Mockito.when(light2.getType()).thenReturn(LightType.HUE_LUX_BULB);
        Mockito.when(light2.isOn()).thenAnswer(invocation -> this.lightStates.get(2));

        Mockito.when(philipsHueMock.getLights()).thenReturn(Arrays.asList(light, light2));

        // Keep track of light state for more realistic behavior when mocking.
        Mockito.doAnswer(invocation -> this.lightStates.put(invocation.getArgumentAt(0, Integer.class), true))
                .when(philipsHueMock).turnOnLight(Matchers.anyInt());
        Mockito.doAnswer(invocation -> this.lightStates.put(invocation.getArgumentAt(0, Integer.class), false))
                .when(philipsHueMock).turnOffLight(Matchers.anyInt());

        return philipsHueMock;
    }
}
