package sketchy.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import sketchy.Constants;
import sketchy.util.TypeUtil;

/**
 * ClassVisitor to read local variable table from bytecode and to know
 * which local variables we have in every method body.
 */
public class LocalVariableTableClassVisitor extends ClassVisitor {

    private final String classBinName;

    public LocalVariableTableClassVisitor(ClassVisitor cv, String classBinName) {
        super(Constants.ASM_VERSION, cv);
        this.classBinName = classBinName;
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String desc,
            String signature,
            String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name,
                desc, signature, exceptions);
        return new LocalVariableTableMethodVisitor(
                mv,
                TypeUtil.toFullMethodName(classBinName, name, desc));
    }
}
