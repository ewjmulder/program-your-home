package com.programyourhome.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.ServerConfig;

public abstract class AbstractProgramYourHomeController {

    @Autowired
    private ServerConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getConfig();
    }

    private List<Activity> getActivities() {
        return this.getServerConfig().getActivitiesConfig().getActivities();
    }

    protected Optional<Activity> getActivity(final int id) {
        return this.getActivities().stream().filter(activity -> activity.getId() == id).findFirst();
    }

    protected Optional<Activity> getActivity(final String name) {
        return this.getActivities().stream().filter(activity -> activity.getName().equals(name)).findFirst();
    }

}
