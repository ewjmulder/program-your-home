// Based on: https://github.com/lambdista/try/blob/master/src/main/java/com/lambdista/util/FailableSupplier.java
package com.programyourhome.common.functional;

/**
 * This class is semantically the same as the {@link java.util.function.Consumer} class apart from the fact that
 * its {@link FailableConsumer#accept()} method may throw an {@link java.lang.Exception}.
 * Also, this interface lacks the andThen utility method.
 *
 * @author Alessandro Lacava
 * @since 2014-06-20
 */
@FunctionalInterface
public interface FailableConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws Exception if it fails
     */
    void accept(T t) throws Exception;

}