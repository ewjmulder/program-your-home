package com.programyourhome.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.common.functional.RunnableWithException;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.ServerConfig;
import com.programyourhome.server.model.ServiceResult;
import com.programyourhome.server.model.compose.ServiceResultSuccess;
import com.programyourhome.server.model.compose.ServiceResultTry;

public abstract class AbstractProgramYourHomeController {

    @Autowired
    private ServerConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getConfig();
    }

    // TODO: does this stuff belong here?
    private List<Activity> getActivities() {
        return this.getServerConfig().getActivitiesConfig().getActivities();
    }

    protected Optional<Activity> findActivity(final int id) {
        return this.getActivities().stream().filter(activity -> activity.getId() == id).findFirst();
    }

    protected Optional<Activity> findActivity(final String name) {
        return this.getActivities().stream().filter(activity -> activity.getName().equals(name)).findFirst();
    }

    // TODO: refactor to last param a function from Id extends Number to Optional<T> so we can use :: notation on caller
    public static <T> ServiceResultTry<T> find(final String type, final Number id, final FailableSupplier<Optional<T>> supplier) {
        return new ServiceResultSuccess<T>(type).find(id, supplier);
    }

    // TODO: check these!
    public static ServiceResult doRun(final RunnableWithException<Exception> runnable) {
        return doRun(runnable, "Exception while excecuting");
    }

    public static ServiceResult doRun(final RunnableWithException<Exception> runnable, final String errorMessage) {
        // return new ServiceResultSuccess<Void>("").act(runnable, errorMessage);
    }

}
