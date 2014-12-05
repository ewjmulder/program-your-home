package com.programyourhome.ir;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.config.InfraRedConfigHolder;
import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.ir.model.PyhDeviceImpl;
import com.programyourhome.ir.winlirc.WinLIRCClient;

@Component
@PropertySource("classpath:com/programyourhome/config/infra-red/properties/infra-red.properties")
public class InfraRedImpl implements InfraRed {

    // TODO: make asychronous, with internal queueing machanism that waits a small time between IR commands of different devices and
    // a configured time between IR command of the same device (general wait and possible override per key (like POWER)

    @Value("${winlirc.host}")
    private String winlircHost;

    @Value("${winlirc.port}")
    private int winlircPort;

    @Autowired
    private InfraRedConfigHolder configHolder;

    @Autowired
    private WinLIRCClient irClient;

    @PostConstruct
    public void init() throws Exception {
        this.irClient.connect(this.winlircHost, this.winlircPort);
    }

    @Override
    public Collection<PyhDevice> getDevices() {
        return this.configHolder.getConfig().getDevices().stream()
                .map(device -> new PyhDeviceImpl(device))
                .collect(Collectors.toList());
    }

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
    public void pressKeyOnRemote(final int deviceId, final int keyId) {
        // this.irClient.pressRemoteKey(remoteName, key);
    }

}
