package com.programyourhome.server.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.config.ServerConfig;

@Component
public class ConfigHolder {

    @Autowired
    private ConfigLoader configLoader;
    private ServerConfig serverConfig;

    @PostConstruct
    private void init() {
        this.serverConfig = this.configLoader.loadConfig();
    }

    // TODO: caching done, also dynamic loading of underlying xml when changed? (what to do on error? -> not replace)
    public ServerConfig getServerConfig() {
        return this.serverConfig;
    }

}
