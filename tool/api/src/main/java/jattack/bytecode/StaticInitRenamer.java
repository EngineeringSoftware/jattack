package jattack.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import jattack.Constants;
import jattack.data.Data;
import jattack.util.TypeUtil;

/**
 * A {@link ClassVisitor} that renames &lt;clinit&gt; and create a new
 * method, which calls the original &lt;clinit&gt; method.
 * <p>
 * Adapted from https://gitlab.ow2.org/asm/asm/-/blob/master/asm-commons/src/main/java/org/objectweb/asm/commons/StaticInitMerger.java
 */
public class StaticInitRenamer extends ClassVisitor {

    private final String newStaticInitName;
    private String owner;
    private MethodVisitor newClinitVisitor;

    public StaticInitRenamer(final String newStaticInitName, final ClassVisitor classVisitor) {
        super(Constants.ASM_VERSION, classVisitor);
        this.newStaticInitName = newStaticInitName;
    }

    @Override
    public void visit(
            final int version,
            final int access,
            final String name,
            final String signature,
            final String superName,
            final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        // TODO: collect which fields are final and of immutable type
        //  so that we will not need to worry about them when
        //  captureing/resetting values for these fields.
        // Unset any final modifier for fields.
        return super.visitField(access & ~Opcodes.ACC_FINAL, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions) {
        MethodVisitor methodVisitor;
        if ("<clinit>".equals(name)) {
            int newAccess = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC;
            String newName = newStaticInitName;
            methodVisitor = super.visitMethod(newAccess, newName, descriptor, signature, exceptions);

            // Creates a new <clinit> that
            newClinitVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            // 1) invokes the renamed original <clinit>
            newClinitVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    owner,
                    newName,
                    descriptor,
                    false);
            // 2) records the order of class initialization
            if (!Data.hasRecordedClassInitOrder()) {
                newClinitVisitor.visitLdcInsn(TypeUtil.intern2Bin(owner));
                newClinitVisitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Constants.DATA_CLZ_INTERN_NAME,
                        Constants.ADD_CLASS_INIT_METH_NAME,
                        Constants.ADD_CLASS_INIT_METH_DESC,
                        false);
            }
            newClinitVisitor.visitInsn(Opcodes.RETURN);
            newClinitVisitor.visitMaxs(0, 0);
        } else {
            methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        return methodVisitor;
    }
}
