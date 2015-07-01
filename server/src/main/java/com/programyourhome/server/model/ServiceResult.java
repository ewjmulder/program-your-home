package com.programyourhome.server.model;

public class ServiceResult {

    private final boolean success;
    private final Object payload;
    private final String error;

    private ServiceResult(final boolean success, final Object payload, final String error) {
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

    public static ServiceResult success() {
        return new ServiceResult(true, null, null);
    }

    public static ServiceResult success(final Object payload) {
        return new ServiceResult(true, payload, null);
    }

    public static ServiceResult error(final String message) {
        return new ServiceResult(false, null, message);
    }

}
