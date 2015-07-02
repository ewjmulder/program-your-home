package com.programyourhome.server.model.compose;

import java.util.Optional;
import java.util.function.Function;

import com.programyourhome.common.functional.FailableConsumer;
import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.server.model.ServiceResult;

public interface ServiceResultTry<T> {

    public boolean isError();

    public boolean isSuccess();

    public ServiceResult result();

    public ServiceResultTry<T> find(final Number id, final FailableSupplier<Optional<T>> supplier);

    public ServiceResultTry<T> filter(final Function<T, Boolean> predicate);

    public ServiceResultTry<T> filter(final Function<T, Boolean> predicate, final String errorMessage);

    public <U> ServiceResultTry<U> map(final Function<T, U> function);

    public ServiceResult act(final FailableConsumer<T> runnable);

    public ServiceResult act(final FailableConsumer<T> runnable, final String errorMessage);

}
