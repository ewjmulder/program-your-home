package com.programyourhome.environment.mock;

public class HelloWorldClassLoader extends ClassLoader {
    @Override
    protected Class findClass(final String name) throws ClassNotFoundException {
        System.out.println("Finding class name: " + name);
        if (name.endsWith("Annotated")) {
            try {
                final byte[] b = PyhDeviceAnnotatedDump.dump();
                return this.defineClass(name, b, 0, b.length);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return super.findClass(name);
    }
}