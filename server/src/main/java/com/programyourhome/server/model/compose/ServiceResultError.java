package com.programyourhome.server.model.compose;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.programyourhome.common.functional.FailableConsumer;
import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.server.model.ServiceResult;

public class ServiceResultError<T> implements ServiceResultTry<T> {

    private final Log log = LogFactory.getLog(this.getClass());

    private final String errorMessage;

    public ServiceResultError(final String message) {
        this.errorMessage = message;
        this.log.info(this.errorMessage);
    }

    public ServiceResultError(final String action, final String type, final Exception e) {
        this(action + " " + type + "' caused an exception: ", e);
    }

    public ServiceResultError(final String message, final Exception e) {
        this.errorMessage = message + ": " + this.toString(e);
        this.log.error(this.errorMessage, e);
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public ServiceResult result() {
        return ServiceResult.error(this.errorMessage);
    }

    @Override
    public ServiceResultTry<T> find(final Number id, final FailableSupplier<Optional<T>> supplier) {
        return this;
    }

    @Override
    public ServiceResultTry<T> filter(final Function<T, Boolean> predicate) {
        return this;
    }

    @Override
    public ServiceResultTry<T> filter(final Function<T, Boolean> predicate, final String errorMessage) {
        return this;
    }

    @Override
    // The value type doesn't matter for an Error.
    @SuppressWarnings("unchecked")
    public <U> ServiceResultTry<U> map(final Function<T, U> function) {
        return (ServiceResultTry<U>) this;
    }

    @Override
    public ServiceResult act(final FailableConsumer<T> runnable) {
        return this.result();
    }

    @Override
    public ServiceResult act(final FailableConsumer<T> runnable, final String errorMessage) {
        return this.result();
    }

    private String toString(final Exception e) {
        return e.getClass().getSimpleName() + " - " + e.getMessage();
    }

}
