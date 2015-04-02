package com.programyourhome.environment.mock;

import java.math.BigDecimal;
import java.util.Random;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.sensors.SunDegreeSensor;

@Configuration
public class SunDegreePollerMock extends PyhMock {

    private final Random random;

    public SunDegreePollerMock() {
        this.random = new Random();
    }

    @Bean
    public SunDegreeSensor createSunDegreeSensorMock() {
        final SunDegreeSensor sunDegreeSensorMock = this.createMock(SunDegreeSensor.class);
        // Random value with minimum of -89.9 and maximum of 89.9
        Mockito.when(sunDegreeSensorMock.getSunDegree()).then(invocation -> BigDecimal.valueOf(this.random.nextInt(1798) - 899, 1));
        return sunDegreeSensorMock;
    }
}
