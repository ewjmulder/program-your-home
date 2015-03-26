package com.programyourhome.environment.mock;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mockito;
import org.mockito.listeners.InvocationListener;
import org.objectweb.asm.util.ASMifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.ir.model.PyhDevice;

public abstract class PyhMock {

    protected final Log log = LogFactory.getLog(this.getClass());

    private final InvocationListener invocationLogger;

    public PyhMock() {
        this.invocationLogger = methodInvocationReport -> this.log.debug("Mock method invocation: " + methodInvocationReport);
    }

    // TODO: make this dynamic!
    // for instance Javassist: http://ayoubelabbassi.blogspot.nl/2011/01/how-to-add-annotations-at-runtime-to.html
    // http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/html/javassist/bytecode/annotation/package-summary.html
    // Technically, you can, via a bytecode manipulation library - CGLIB, javassist, asm, bcel and the likes.

    public static void main(final String[] args) throws Exception {
        ASMifier.main(new String[] { "com.programyourhome.environment.mock.PyhDeviceAnnotated" });
    }

    @Bean
    @Primary
    public ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final Class clazz = new HelloWorldClassLoader().loadClass("com.programyourhome.environment.mock.PyhDeviceAnnotated");
            objectMapper.addMixInAnnotations(PyhDevice.class, clazz);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return objectMapper;
    }

    @PostConstruct
    public void init() {
        this.log.info(this.getClass().getSimpleName() + " initialized");
    }

    protected <T> T createMock(final Class<T> clazz) {
        return Mockito.mock(clazz, Mockito.withSettings().invocationListeners(this.invocationLogger));
    }

}
