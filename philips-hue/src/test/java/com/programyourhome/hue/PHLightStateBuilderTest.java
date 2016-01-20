package com.programyourhome.hue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

import com.philips.lighting.model.PHLight;

public class PHLightStateBuilderTest {

    private PHLightStateBuilder phLightStateBuilder;

    @Before
    public void init() {
        final PHLight phLight = Mockito.mock(PHLight.class, Answers.RETURNS_DEEP_STUBS);
        this.phLightStateBuilder = new PHLightStateBuilder(phLight);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotSetColorMoodTwice() {
        this.phLightStateBuilder.colorTemperature(0).colorXY(0, 0);
    }

}
