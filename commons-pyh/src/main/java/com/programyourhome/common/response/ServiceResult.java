package com.programyourhome.common.response;

public interface ServiceResult<T> {

    /**
     * Whether or not the service call completed successfully.
     *
     * @return success (true) or error (false)
     */
    public boolean isSuccess();

    /**
     * Get an error message.
     * Will be null on success, should be filled on error (isSuccess is false).
     *
     * @return an error message
     */
    public String getError();

    /**
     * Get the payload of the service result.
     * Will be null on error (isSuccess is false).
     * On success, this can be either be null (void service call) or filled with a service return value.
     *
     * @return the payload
     */
    public T getPayload();

}