package com.programyourhome.ir;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.model.Remote;

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
    public Collection<Remote> getRemotes() {
        // Use this construction so we don't have issues with generics.
        return this.irClient.getRemotes().stream()
                .collect(Collectors.toList());
    }

    @Override
    public void pressRemoteKey(final String remoteName, final String key) {
        this.irClient.pressRemoteKey(remoteName, key);
    }

}
