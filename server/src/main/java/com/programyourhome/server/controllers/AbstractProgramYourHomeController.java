package com.programyourhome.server.controllers;

import java.util.Optional;

import javax.inject.Inject;

import com.programyourhome.common.functional.FailableFunction;
import com.programyourhome.common.functional.FailableRunnable;
import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.ServerConfig;
import com.programyourhome.server.response.ServiceResult;
import com.programyourhome.server.response.ServiceResultSuccess;
import com.programyourhome.server.response.ServiceResultTry;

public abstract class AbstractProgramYourHomeController {

    @Inject
    private ServerConfigHolder configHolder;

    protected ServerConfig getServerConfig() {
        return this.configHolder.getConfig();
    }

    public <T, N extends Number> ServiceResultTry<T> find(final String type, final N id, final FailableFunction<N, Optional<T>> finder) {
        return new ServiceResultSuccess<T>(type).find(() -> finder.apply(id));
    }

    public <T> ServiceResult<T> produce(final String type, final FailableSupplier<T> supplier) {
        return this.produce(type, supplier, "Exception while producing.");
    }

    public <T> ServiceResult<T> produce(final String type, final FailableSupplier<T> supplier, final String errorMessage) {
        return new ServiceResultSuccess<T>(type).produce(value -> supplier.get(), errorMessage);
    }

    public <T> ServiceResult<Void> run(final FailableRunnable<Exception> runnable) {
        return this.run(runnable, "Exception while running.");
    }

    public <T> ServiceResult<Void> run(final FailableRunnable<Exception> runnable, final String errorMessage) {
        return new ServiceResultSuccess<Void>("Run wrapper").process(value -> runnable.run(), errorMessage);
    }

}
