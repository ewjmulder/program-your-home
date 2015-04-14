package com.programyourhome.server.model;

public class ServiceResult {

    private final boolean success;
    private final String error;

    private ServiceResult(final boolean success) {
        this.success = success;
        this.error = null;
    }

    private ServiceResult(final boolean success, final String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getError() {
        return this.error;
    }

    public static ServiceResult success() {
        return new ServiceResult(true);
    }

    public static ServiceResult error(final String message) {
        return new ServiceResult(false, message);
    }

}
