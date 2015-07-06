package com.programyourhome.server.model.compose;

import java.util.Optional;
import java.util.function.Function;

import com.programyourhome.common.functional.FailableConsumer;
import com.programyourhome.common.functional.FailableFunction;
import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.server.model.ServiceResult;

public interface ServiceResultTry<T> {

    public boolean isError();

    public boolean isSuccess();

    public ServiceResult result();

    public ServiceResultTry<T> find(final FailableSupplier<Optional<T>> supplier);

    public ServiceResultTry<T> ensure(final Function<T, Boolean> predicate);

    public ServiceResultTry<T> ensure(final Function<T, Boolean> predicate, final String errorMessage);

    public <U> ServiceResultTry<U> map(final Function<T, U> function);

    public <U> ServiceResultTry<U> map(final Function<T, U> function, String newType);

    public <U> ServiceResultTry<U> flatMap(final Function<T, Optional<U>> function);

    public <U> ServiceResultTry<U> flatMap(final Function<T, Optional<U>> function, final String newType);

    public ServiceResult produce(final FailableFunction<T, Object> function);

    public ServiceResult produce(final FailableFunction<T, Object> function, final String errorMessage);

    public ServiceResult process(final FailableConsumer<T> consumer);

    public ServiceResult process(final FailableConsumer<T> consumer, final String errorMessage);

}
