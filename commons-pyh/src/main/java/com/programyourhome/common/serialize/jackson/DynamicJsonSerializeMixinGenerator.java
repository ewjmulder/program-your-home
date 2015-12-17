package com.programyourhome.common.serialize.jackson;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Component
public class DynamicJsonSerializeMixinGenerator {

    private static final String PACKAGE = DynamicJsonSerializeMixinGenerator.class.getPackage().getName() + ".dynamic";
    private static final String POSTFIX = "Annotated";

    @Inject
    private ByteArrayClassLoader classDefinitionLoader;

    private final Map<Class<?>, Class<?>> generatedClasses;

    public DynamicJsonSerializeMixinGenerator() {
        this.generatedClasses = new HashMap<>();
    }

    public Class<?> generateClass(final Class<?> forType) {
        if (this.generatedClasses.containsKey(forType)) {
            return this.generatedClasses.get(forType);
        }

        final ClassWriter classWriter = new ClassWriter(0);

        // Build a Java 8 type.
        final int JAVA_VERSION_8 = 52;
        // We want a public interface, so we'll have to use public, abstract & interface.
        final int modifiers = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;
        final String generatedClassName = PACKAGE + "." + forType.getSimpleName() + POSTFIX;
        classWriter.visit(JAVA_VERSION_8, modifiers, generatedClassName.replace('.', '/'), null, this.getFullyQualified(Object.class),
                new String[] { this.getFullyQualified(forType) });

        final AnnotationVisitor annotationVisitor = classWriter.visitAnnotation(this.getFullyQualifiedEnhanced(JsonSerialize.class), true);
        annotationVisitor.visit("as", Type.getType(this.getFullyQualifiedEnhanced(forType)));
        annotationVisitor.visitEnd();

        classWriter.visitEnd();

        final byte[] classBytes = classWriter.toByteArray();
        final Class<?> generatedClass = this.classDefinitionLoader.defineClass(generatedClassName, classBytes);
        this.generatedClasses.put(forType, generatedClass);
        return generatedClass;
    }

    private String getFullyQualifiedEnhanced(final Class<?> clazz) {
        return "L" + this.getFullyQualified(clazz) + ";";
    }

    private String getFullyQualified(final Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

}
