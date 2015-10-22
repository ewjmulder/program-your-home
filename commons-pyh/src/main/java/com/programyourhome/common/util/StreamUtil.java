package com.programyourhome.common.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtil {

    public StreamUtil() {
    }

    /**
     * Convenience method to create a Stream from an Iterator.
     * Default value for the use if parallel is false.
     *
     * @param iterable the iterable
     * @return a stream over the iterable
     */
    public static <T> Stream<T> fromIterable(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

}
