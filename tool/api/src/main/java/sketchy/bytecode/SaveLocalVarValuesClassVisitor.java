package sketchy.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import sketchy.Constants;
import sketchy.util.TypeUtil;

/**
 * ClassVisitor to insert instructions to save values of local
 * variables right before each {@code eval()}.
 */
public class SaveLocalVarValuesClassVisitor extends ClassVisitor {

    private final String classBinName;

    public SaveLocalVarValuesClassVisitor(ClassVisitor cv, String classBinName) {
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
        return new SaveLocalVarValuesMethodVisitor(
                mv,
                TypeUtil.bin2Intern(classBinName),
                TypeUtil.toFullMethodName(classBinName, name, desc),
                (access & Opcodes.ACC_STATIC) != 0);
    }
}
