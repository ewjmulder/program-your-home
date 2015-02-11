package com.programyourhome.server.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.programyourhome.config.Activity;
import com.programyourhome.config.ServerConfig;
import com.programyourhome.server.config.ServerConfigHolder;

public abstract class AbstractProgramYourHomeController {

    @Autowired
    private ServerConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getConfig();
    }

    protected Optional<Activity> getActivity(final int id) {
        return this.getServerConfig().getActivities().stream().filter(activity -> activity.getId() == id).findFirst();
    }

    protected Optional<Activity> getActivity(final String name) {
        return this.getServerConfig().getActivities().stream().filter(activity -> activity.getName().equals(name)).findFirst();
    }

}
