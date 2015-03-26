package com.programyourhome.environment.mock;

import org.mockito.asm.Type;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.programyourhome.ir.model.PyhDevice;

public class PyhDeviceAnnotatedDump implements Opcodes {

    public static byte[] dump() throws Exception {
        final ClassWriter classWriter = new ClassWriter(0);

        // 52 = Java 8
        classWriter.visit(52, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, PyhDevice.class.getName() + "Annotated", null, "java/lang/Object",
                new String[] { PyhDevice.class.getName() });

        final AnnotationVisitor annotationVisitor = classWriter.visitAnnotation("L" + JsonSerialize.class.getName().replace(".", "/") + ";", true);
        annotationVisitor.visit("as", Type.getType("L" + PyhDevice.class.getName().replace(".", "/") + ";"));
        annotationVisitor.visitEnd();

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    /*
     * public static byte[] dump() throws Exception {
     * final ClassWriter cw = new ClassWriter(0);
     * final FieldVisitor fv;
     * final MethodVisitor mv;
     * final AnnotationVisitor av0;
     * cw.visit(52, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "com/programyourhome/environment/mock/PyhDeviceAnnotated", null, "java/lang/Object",
     * new String[] { "com/programyourhome/ir/model/PyhDevice" });
     * {
     * av0 = cw.visitAnnotation("Lcom/fasterxml/jackson/databind/annotation/JsonSerialize;", true);
     * av0.visit("as", Type.getType("Lcom/programyourhome/ir/model/PyhDevice;"));
     * av0.visitEnd();
     * }
     * cw.visitEnd();
     * return cw.toByteArray();
     * }
     */
}
