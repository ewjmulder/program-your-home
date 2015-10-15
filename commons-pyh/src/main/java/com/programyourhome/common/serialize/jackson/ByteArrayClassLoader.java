package com.programyourhome.common.serialize.jackson;

import org.springframework.stereotype.Component;

@Component
public class ByteArrayClassLoader extends ClassLoader {

    public Class<?> defineClass(final String name, final byte[] bytes) {
        return this.defineClass(name, bytes, 0, bytes.length);
    }

}
