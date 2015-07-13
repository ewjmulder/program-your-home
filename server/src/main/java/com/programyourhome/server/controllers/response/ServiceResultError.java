package com.programyourhome.server.controllers.response;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.programyourhome.common.functional.FailableConsumer;
import com.programyourhome.common.functional.FailableFunction;
import com.programyourhome.common.functional.FailableSupplier;

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
    public ServiceResult<T> result() {
        return ServiceResult.error(this.errorMessage);
    }

    @Override
    public ServiceResultTry<T> find(final FailableSupplier<Optional<T>> supplier) {
        return this;
    }

    @Override
    public ServiceResultTry<T> ensure(final Function<T, Boolean> predicate) {
        return this;
    }

    @Override
    public ServiceResultTry<T> ensure(final Function<T, Boolean> predicate, final String errorMessage) {
        return this;
    }

    // The value type doesn't matter for an Error, so all (flat)map and produce and process methods can just return a casted value of this.

    @Override
    @SuppressWarnings("unchecked")
    public <U> ServiceResultTry<U> map(final Function<T, U> function) {
        return (ServiceResultTry<U>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> ServiceResultTry<U> map(final Function<T, U> function, final String newType) {
        return (ServiceResultTry<U>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> ServiceResultTry<U> flatMap(final Function<T, Optional<U>> function) {
        return (ServiceResultTry<U>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> ServiceResultTry<U> flatMap(final Function<T, Optional<U>> function, final String newType) {
        return (ServiceResultTry<U>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> ServiceResult<R> produce(final FailableFunction<T, R> function) {
        return (ServiceResult<R>) this.result();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> ServiceResult<R> produce(final FailableFunction<T, R> function, final String errorMessage) {
        return (ServiceResult<R>) this.result();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResult<Void> process(final FailableConsumer<T> consumer) {
        return (ServiceResult<Void>) this.result();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResult<Void> process(final FailableConsumer<T> consumer, final String errorMessage) {
        return (ServiceResult<Void>) this.result();
    }

    private String toString(final Exception e) {
        return e.getClass().getSimpleName() + " - " + e.getMessage();
    }

}
