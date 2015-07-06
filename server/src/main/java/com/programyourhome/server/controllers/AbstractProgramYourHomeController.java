package com.programyourhome.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.programyourhome.common.functional.FailableFunction;
import com.programyourhome.common.functional.FailableRunnable;
import com.programyourhome.common.functional.FailableSupplier;
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

    public <T, N extends Number> ServiceResultTry<T> find(final String type, final N id, final FailableFunction<N, Optional<T>> finder) {
        return new ServiceResultSuccess<T>(type).find(() -> finder.apply(id));
    }

    public <T> ServiceResult produce(final String type, final FailableSupplier<T> supplier) {
        return this.produce(type, supplier, "Exception while producing.");
    }

    public <T> ServiceResult produce(final String type, final FailableSupplier<T> supplier, final String errorMessage) {
        return new ServiceResultSuccess<T>(type).produce(value -> supplier.get(), errorMessage);
    }

    public ServiceResult run(final FailableRunnable<Exception> runnable) {
        return this.run(runnable, "Exception while running.");
    }

    public ServiceResult run(final FailableRunnable<Exception> runnable, final String errorMessage) {
        return new ServiceResultSuccess<Void>("Run wrapper").process(value -> runnable.run(), errorMessage);
    }

}
