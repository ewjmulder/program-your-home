package com.programyourhome.server.model.compose;

import java.util.Optional;
import java.util.function.Function;

import com.programyourhome.common.functional.FailableConsumer;
import com.programyourhome.common.functional.FailableFunction;
import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.server.model.ServiceResult;

public class ServiceResultSuccess<T> implements ServiceResultTry<T> {

    private final String type;
    private T value;

    public ServiceResultSuccess(final String type) {
        this(type, null);
    }

    public ServiceResultSuccess(final String type, final T value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public ServiceResult<T> result() {
        return ServiceResult.success(this.value);
    }

    @Override
    public ServiceResultTry<T> find(final FailableSupplier<Optional<T>> supplier) {
        if (this.value != null) {
            throw new IllegalStateException("There is already a value present.");
        }
        ServiceResultTry<T> result = this;
        try {
            final Optional<T> optionalResult = supplier.get();
            if (optionalResult.isPresent()) {
                this.value = optionalResult.get();
            } else {
                result = new ServiceResultError<T>(this.type + " could not be found.");
            }
        } catch (final Exception e) {
            result = new ServiceResultError<T>("Finding", this.type, e);
        }
        return result;
    }

    @Override
    public ServiceResultTry<T> ensure(final Function<T, Boolean> predicate) {
        return this.ensure(predicate, this.type + " is not in the right state.");
    }

    @Override
    public ServiceResultTry<T> ensure(final Function<T, Boolean> predicate, final String errorMessage) {
        ServiceResultTry<T> result = this;
        try {
            if (!predicate.apply(this.value)) {
                result = new ServiceResultError<T>(errorMessage);
            }
        } catch (final Exception e) {
            result = new ServiceResultError<T>("Filtering", this.type, e);
        }
        return result;
    }

    @Override
    public <U> ServiceResultTry<U> map(final Function<T, U> function) {
        return this.map(function, this.type);
    }

    @Override
    public <U> ServiceResultTry<U> map(final Function<T, U> function, final String newType) {
        ServiceResultTry<U> result;
        try {
            final U mappedValue = function.apply(this.value);
            result = new ServiceResultSuccess<U>(newType, mappedValue);
        } catch (final Exception e) {
            result = new ServiceResultError<U>("Mapping", this.type, e);
        }
        return result;
    }

    @Override
    public <U> ServiceResultTry<U> flatMap(final Function<T, Optional<U>> function) {
        return this.flatMap(function, this.type);
    }

    @Override
    public <U> ServiceResultTry<U> flatMap(final Function<T, Optional<U>> function, final String newType) {
        ServiceResultTry<U> result;
        try {
            result = new ServiceResultSuccess<U>(newType).find(() -> function.apply(this.value));
        } catch (final Exception e) {
            result = new ServiceResultError<U>("Flatmapping", this.type, e);
        }
        return result;
    }

    @Override
    public <R> ServiceResult<R> produce(final FailableFunction<T, R> function) {
        return this.produce(function, "Exception while producing output.");
    }

    @Override
    public <R> ServiceResult<R> produce(final FailableFunction<T, R> function, final String errorMessage) {
        ServiceResult<R> result;
        try {
            final R payload = function.apply(this.value);
            result = ServiceResult.success(payload);
        } catch (final Exception e) {
            result = new ServiceResultError<R>(errorMessage, e).result();
        }
        return result;
    }

    @Override
    public ServiceResult<Void> process(final FailableConsumer<T> consumer) {
        return this.process(consumer, "Exception while processing.");
    }

    @Override
    public ServiceResult<Void> process(final FailableConsumer<T> consumer, final String errorMessage) {
        ServiceResult<Void> result;
        try {
            consumer.accept(this.value);
            result = ServiceResult.success();
        } catch (final Exception e) {
            result = new ServiceResultError<Void>(errorMessage, e).result();
        }
        return result;
    }
}
