package com.programyourhome.environment.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.hue.PhilipsHue;

@Configuration
public class PhilipsHueMock extends PyhMock {

    @Bean
    public PhilipsHue createPhilipsHueMock() {
        final PhilipsHue philipsHueMock = this.createMock(PhilipsHue.class);
        return philipsHueMock;
    }
}
