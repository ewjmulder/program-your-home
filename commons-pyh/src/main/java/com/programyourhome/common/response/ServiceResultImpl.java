package com.programyourhome.common.response;

public class ServiceResultImpl<T> implements ServiceResult<T> {

    private final boolean success;
    private final T payload;
    private final String error;

    private ServiceResultImpl(final boolean success, final T payload, final String error) {
        this.success = success;
        this.payload = payload;
        this.error = error;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public T getPayload() {
        return this.payload;
    }

    @Override
    public String getError() {
        return this.error;
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResultImpl<>(true, null, null);
    }

    public static <T> ServiceResult<T> success(final T payload) {
        return new ServiceResultImpl<>(true, payload, null);
    }

    public static <T> ServiceResult<T> error(final String message) {
        return new ServiceResultImpl<>(false, null, message);
    }

}
