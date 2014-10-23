package com.programyourhome.ir;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.model.IRDevice;

@Component
public class InfraRedImpl implements InfraRed, InitializingBean {

    @Autowired
    private WinLIRCClient irClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO: get from some kind of properties (with Spring?)
        this.irClient.connect("192.168.2.47", 8765);
    }

    @Override
    public List<IRDevice> getDevices() {
        // TODO TODOTODO
        return null;
    }

    @Override
    public void sendCommand(final String remote, final String key) {
        this.irClient.sendIRCommand(remote, key);
    }

}
