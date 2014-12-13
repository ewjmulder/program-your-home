package com.programyourhome.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.programyourhome.config.ServerConfig;
import com.programyourhome.server.config.ServerConfigHolder;

public class AbstractProgramYourHomeController {

    @Autowired
    private ServerConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getConfig();
    }
}
