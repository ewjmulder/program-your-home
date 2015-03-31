package com.programyourhome.environment.mock;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Component
public class DynamicJsonSerializeMixinGenerator implements Opcodes {

    private static final String PACKAGE = "com.programyourhome.environment.mock";
    private static final String POSTFIX = "Annotated";

    @Autowired
    private ByteArrayClassLoader classDefinitionLoader;

    public Class<?> generateClass(final Class<?> forType) {
        final ClassWriter classWriter = new ClassWriter(0);

        // Build a Java 8 type.
        final int JAVA_VERSION_8 = 52;
        // We want a public interface, so we'll have to use public, abstract & interface.
        final int modifiers = ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE;
        final String generatedClassName = PACKAGE + "." + forType.getSimpleName() + POSTFIX;
        classWriter.visit(JAVA_VERSION_8, modifiers, generatedClassName.replace('.', '/'), null, this.getFullyQualified(Object.class),
                new String[] { this.getFullyQualified(forType) });

        final AnnotationVisitor annotationVisitor = classWriter.visitAnnotation(this.getFullyQualifiedEnhanced(JsonSerialize.class), true);
        annotationVisitor.visit("as", Type.getType(this.getFullyQualifiedEnhanced(forType)));
        annotationVisitor.visitEnd();

        classWriter.visitEnd();

        final byte[] classBytes = classWriter.toByteArray();
        return this.classDefinitionLoader.defineClass(generatedClassName, classBytes);
    }

    private String getFullyQualifiedEnhanced(final Class<?> clazz) {
        return "L" + this.getFullyQualified(clazz) + ";";
    }

    private String getFullyQualified(final Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

}
