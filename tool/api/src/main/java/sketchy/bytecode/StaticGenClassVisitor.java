package sketchy.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import sketchy.Constants;
import sketchy.util.TypeUtil;

/**
 * Class visitor for static generation.
 */
public class StaticGenClassVisitor extends ClassVisitor {

    private final String classBinName;

    public StaticGenClassVisitor(ClassVisitor cv, String classBinName) {
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
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new StaticGenMethodVisitor(
                mv,
                TypeUtil.toFullMethodName(classBinName, name, desc),
                access,
                name,
                desc,
                signature,
                exceptions);
    }
}
