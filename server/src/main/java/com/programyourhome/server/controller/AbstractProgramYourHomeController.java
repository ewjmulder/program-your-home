package com.programyourhome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.programyourhome.config.ServerConfig;
import com.programyourhome.server.config.ConfigHolder;

public class AbstractProgramYourHomeController {

    @Autowired
    private ConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getServerConfig();
    }
}
