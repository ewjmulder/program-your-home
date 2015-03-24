package com.programyourhome.server.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhDevice;

@Component
@ConditionalOnProperty("environment.mock")
public class InfraRedMock implements InfraRed {

    private final Log log = LogFactory.getLog(this.getClass());

    private Collection<PyhDevice> mockDevices;

    public InfraRedMock() {
        this.mockDevices = Arrays.asList(new PyhDevice() {
            @Override
            public boolean isVolumeDevice() {
                return false;
            }

            @Override
            public boolean isSkipDevice() {
                return false;
            }

            @Override
            public boolean isRecordDevice() {
                return false;
            }

            @Override
            public boolean isPowerDevice() {
                return false;
            }

            @Override
            public boolean isPlayDevice() {
                return false;
            }

            @Override
            public boolean isMenuDevice() {
                return false;
            }

            @Override
            public boolean isInputDevice() {
                return false;
            }

            @Override
            public boolean isChannelDevice() {
                return false;
            }

            @Override
            public String getName() {
                return "Mock Device";
            }

            @Override
            public List<String> getInputs() {
                return Arrays.asList("Mock input 1", "Mock input 2");
            }

            @Override
            public int getId() {
                return 1;
            }

            @Override
            public List<String> getExtraKeys() {
                return Arrays.asList("Mock extra key 1", "Mock extra key 2");
            }

            @Override
            public String getDescription() {
                return "Mock device for testing without a live environment";
            }
        });
    }

    @PostConstruct
    public void init() {
        this.log.info("InfraRed Mock initialized");
    }

    @Override
    public Collection<PyhDevice> getDevices() {
        return this.mockDevices;
    }

    // TODO: Log method calls.
    @Override
    public void turnOn(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOff(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setInput(final int deviceId, final String input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void volumeUp(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void volumeDown(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void volumeMute(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void channelUp(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void channelDown(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setChannel(final int deviceId, final int channel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void play(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fastForward(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rewind(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void skipNext(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void skipPrevious(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void record(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuToggle(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuSelect(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuBack(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuUp(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuDown(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuLeft(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void menuRight(final int deviceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pressKeyOnRemote(final int deviceId, final int keyId) {
        // TODO Auto-generated method stub

    }

}
