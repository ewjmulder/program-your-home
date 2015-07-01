package com.programyourhome.server.model.compose;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.programyourhome.common.functional.FailableSupplier;
import com.programyourhome.common.functional.RunnableWithException;
import com.programyourhome.server.model.ServiceResult;

public class ServiceResultComposer<T> {

    private final Log log = LogFactory.getLog(this.getClass());

    private final String type;
    private T value;
    private boolean failed;
    private String errorMessage;

    public ServiceResultComposer(final String type) {
        this.type = type;
        this.value = null;
        this.failed = false;
        this.errorMessage = null;
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public ServiceResult result() {
        ServiceResult result;
        if (this.failed) {
            result = ServiceResult.error(this.errorMessage);
        } else {
            result = ServiceResult.success(this.value);
        }
        return result;
    }

    public static <T> ServiceResultComposer<T> create(final String type) {
        return new ServiceResultComposer<T>(type);
    }

    public ServiceResultComposer<T> find(final String identifier, final FailableSupplier<Optional<T>> supplier) {
        try {
            final Optional<T> optionalResult = supplier.get();
            if (optionalResult.isPresent()) {
                this.value = optionalResult.get();
            } else {
                this.fail(this.type + " with identifier: '" + identifier + "' could not be found.");
            }
        } catch (final Exception e) {
            this.fail("Finding", e);
        }
        return this;
    }

    // TODO: map T -> U, flatMap T -> Option<U>, filter T -> Boolean (only fail possibility), others?

    // Also run non-static! (use that for static version)
    public static ServiceResult run(final RunnableWithException<Exception> runnable) {
        return run(runnable, "Exception while excecuting");
    }

    public static ServiceResult run(final RunnableWithException<Exception> runnable, final String errorMessage) {
        ServiceResult result;
        try {
            runnable.run();
            result = ServiceResult.success();
        } catch (final Exception e) {
            result = ServiceResult.error(errorMessage + ": " + toString(e));
        }
        return result;
    }

    public ServiceResultComposer<T> fail(final String message) {
        this.failed = true;
        this.errorMessage = message;
        this.log.info(this.errorMessage);
        return this;
    }

    public ServiceResultComposer<T> fail(final String action, final Exception e) {
        this.failed = true;
        this.errorMessage = action + " " + this.type + "' caused an exception: " + toString(e);
        this.log.error(this.errorMessage, e);
        return this;
    }

    private static String toString(final Exception e) {
        return e.getClass().getSimpleName() + " - " + e.getMessage();
    }
}
