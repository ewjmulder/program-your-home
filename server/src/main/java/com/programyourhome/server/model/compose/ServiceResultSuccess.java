package com.programyourhome.server.model.compose;

import java.util.Optional;
import java.util.function.Function;

import com.programyourhome.common.functional.FailableConsumer;
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
    public ServiceResult result() {
        return ServiceResult.success(this.value);
    }

    @Override
    public ServiceResultTry<T> find(final Number id, final FailableSupplier<Optional<T>> supplier) {
        ServiceResultTry<T> result = this;
        try {
            final Optional<T> optionalResult = supplier.get();
            if (optionalResult.isPresent()) {
                this.value = optionalResult.get();
            } else {
                result = new ServiceResultError<T>(this.type + " with identifier: '" + id + "' could not be found.");
            }
        } catch (final Exception e) {
            result = new ServiceResultError<T>("Finding", this.type, e);
        }
        return result;
    }

    @Override
    public ServiceResultTry<T> filter(final Function<T, Boolean> predicate) {
        return this.filter(predicate, "Predicate does not hold for " + this.type + ".");
    }

    @Override
    public ServiceResultTry<T> filter(final Function<T, Boolean> predicate, final String errorMessage) {
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
        ServiceResultTry<U> result;
        try {
            final U mappedValue = function.apply(this.value);
            result = new ServiceResultSuccess<U>(this.type, mappedValue);
        } catch (final Exception e) {
            result = new ServiceResultError<U>("Mapping", this.type, e);
        }
        return result;
    }

    // TODO: internal state consistency checks: eg no find on success that already has a value (would be overridden otherwise)
    // TODO: map T -> U, flatMap T -> Option<U>, filter T -> Boolean (only fail possibility), others?

    // TODO: besides act that acts on a value, have a run that does not use a value, although in that case the type is irrelevant,
    // so maybe static somewhere else?

    @Override
    public ServiceResult act(final FailableConsumer<T> consumer) {
        return this.act(consumer, "Exception while excecuting");
    }

    @Override
    public ServiceResult act(final FailableConsumer<T> consumer, final String errorMessage) {
        ServiceResult result;
        try {
            consumer.accept(this.value);
            result = ServiceResult.success();
        } catch (final Exception e) {
            result = new ServiceResultError<T>(errorMessage, e).result();
        }
        return result;
    }

}
