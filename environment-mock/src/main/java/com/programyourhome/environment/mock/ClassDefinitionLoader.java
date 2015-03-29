package com.programyourhome.environment.mock;

import org.springframework.stereotype.Component;

@Component
public class ClassDefinitionLoader extends ClassLoader {

    public Class<?> defineClass(final String name, final byte[] bytes) {
        return this.defineClass(name, bytes, 0, bytes.length);
    }

}
