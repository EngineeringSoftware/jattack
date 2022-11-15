package jattack.compiler;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import jattack.Config;
import jattack.Constants;
import jattack.bytecode.StaticInitRenamer;
import jattack.util.TypeUtil;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class StaticInitCopyTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(
            ClassLoader classLoader,
            String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        if (isIgnoreClass(className)) {
            return null;
        }

        try {
            byte[] transformedClassfile = isClassRedefinedOrRetransformed(classBeingRedefined) ?
                    retransformClass(classLoader, className, classfileBuffer) :
                    firstTransformClass(classLoader, className, classfileBuffer);

            // DEBUGGING
            // try {
            //     jattack.util.IOUtil.saveBytecodeToFile(
            //             transformedClassfile,
            //             TypeUtil.getSimpleName(TypeUtil.intern2Bin(className)));
            // } catch (java.io.IOException e) {
            //     e.printStackTrace();
            // }

            return transformedClassfile;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to transform.");
        }
    }

    private byte[] firstTransformClass(
            ClassLoader classLoader,
            String className,
            byte[] classfileBuffer) {
        // System.out.println("First transforming " + className + "...");
        return trasnformClass(classfileBuffer);
    }

    private byte[] retransformClass(
            ClassLoader classLoader,
            String className,
            byte[] classfileBuffer) {
        // System.out.println("Retransforming " + className + "...");
        return trasnformClass(classfileBuffer);
    }

    private byte[] trasnformClass(byte[] classfileBuffer) {
        ClassReader cr;
        ClassWriter cw;
        try {
            cr = new ClassReader(classfileBuffer);
            cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            // Make a copy of static initializer block (<clinit>)
            // so we can explictly invoke it to reset the state of
            // the class; also we unset any final modifier of
            // fields to enable resetting.
            ClassVisitor staticInitCV = new StaticInitRenamer(Constants.STATIC_INITIALIZER_COPY_METHOD, cw);
            cr.accept(staticInitCV, 0);
            return cw.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isClassRedefinedOrRetransformed(Class<?> classBeingRedefined) {
        // classBeingRedefined will be null if the class is normally loaded
        return classBeingRedefined != null;
    }

    private boolean isIgnoreClass(String className) {
        String tmplClassName = TypeUtil.bin2Intern(Config.tmplClzFullName);
        return !className.equals(tmplClassName)
                && !className.startsWith(tmplClassName + '$');
    }
}
