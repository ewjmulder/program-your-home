package com.programyourhome.server.controllers;

import javax.inject.Inject;

import com.programyourhome.common.controller.AbstractProgramYourHomeController;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.ServerConfig;

public abstract class AbstractProgramYourHomeServerController extends AbstractProgramYourHomeController {

    @Inject
    private ServerConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getConfig();
    }

}
