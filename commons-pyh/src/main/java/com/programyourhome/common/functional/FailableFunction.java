// Based on: https://github.com/lambdista/try/blob/master/src/main/java/com/lambdista/util/FailableSupplier.java
package com.programyourhome.common.functional;

/**
 * This class is semantically the same as the {@link java.util.function.Function} class apart from the fact that
 * its {@link FailableFunction#apply()} method may throw an {@link java.lang.Exception}.
 * Also, this interface lacks the utility methods of {@link java.util.function.Function}.
 *
 * @author Alessandro Lacava
 * @since 2014-06-20
 */
@FunctionalInterface
public interface FailableFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Exception if it fails
     */
    R apply(T t) throws Exception;
}