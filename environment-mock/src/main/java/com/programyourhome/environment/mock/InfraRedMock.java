package com.programyourhome.environment.mock;

import java.util.Arrays;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhDevice;

@Configuration
public class InfraRedMock extends PyhMock {

    @Bean
    public InfraRed createInfraRedMock() {
        final InfraRed infraRedMock = this.createMock(InfraRed.class);
        final PyhDevice mockDevice = Mockito.mock(PyhDevice.class);
        Mockito.when(mockDevice.getId()).thenReturn(1);
        Mockito.when(mockDevice.getName()).thenReturn("Mock name");
        Mockito.when(mockDevice.getDescription()).thenReturn("Mock description");
        Mockito.when(mockDevice.getInputs()).thenReturn(Arrays.asList("Mock input 1", "Mock input 2"));
        Mockito.when(mockDevice.getExtraKeys()).thenReturn(Arrays.asList("Mock extra key 1", "Mock extra key 2"));
        Mockito.when(infraRedMock.getDevices()).thenReturn(Arrays.asList(mockDevice));
        Mockito.when(infraRedMock.getDevice(1)).thenReturn(mockDevice);
        return infraRedMock;
    }

}
