package com.programyourhome.environment.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.sensors.SunDegreeSensor;

@Configuration
public class SunDegreePollerMock extends PyhMock {

    @Bean
    public SunDegreeSensor createSunDegreeSensorMock() {
        final SunDegreeSensor sunDegreeSensorMock = this.createMock(SunDegreeSensor.class);
        return sunDegreeSensorMock;
    }

}
