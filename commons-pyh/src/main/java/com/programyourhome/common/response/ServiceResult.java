package com.programyourhome.common.response;

public class ServiceResult<T> {

    private final boolean success;
    private final T payload;
    private final String error;

    private ServiceResult(final boolean success, final T payload, final String error) {
        this.success = success;
        this.payload = payload;
        this.error = error;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public Object getPayload() {
        return this.payload;
    }

    public String getError() {
        return this.error;
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(true, null, null);
    }

    public static <T> ServiceResult<T> success(final T payload) {
        return new ServiceResult<>(true, payload, null);
    }

    public static <T> ServiceResult<T> error(final String message) {
        return new ServiceResult<>(false, null, message);
    }

}
