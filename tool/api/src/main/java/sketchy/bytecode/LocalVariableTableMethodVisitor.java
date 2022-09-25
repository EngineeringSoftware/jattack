package sketchy.bytecode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import sketchy.Constants;
import sketchy.data.Data;
import sketchy.util.TypeUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * MethodVisitor to read local variable table from bytecode and to
 * know which local variables we have in every method body.
 */
public class LocalVariableTableMethodVisitor extends MethodVisitor {

    private final String fullMethodName;
    private int currOffset;

    public LocalVariableTableMethodVisitor(MethodVisitor mv, String fullMethodName) {
        super(Constants.ASM_VERSION, mv);
        this.fullMethodName = fullMethodName;
        this.currOffset = -1;
    }

    private final Set<Var> localVariables = new HashSet<>();

    @Override
    public void visitLocalVariable(String name,
            String desc, String sig,
            Label start, Label end, int index) {
        // start.getOffset() gives the offset
        // of the original (before any
        // bytecode manipulation) instruction
        // that follows the first XSTORE that
        // declares the local variable.
        // However, we use exactly the offset
        // of the XSTORE so we decrement 1 or
        // 2 bytes. This approach requires us
        // NOT to insert any instruction
        // between the XSTORE and the original
        // instruction following XSTORE.

        // end.getOffset() gives the offset
        // of the original (before any
        // bytecode manipulation) instruction
        // that follows the last instruction
        // that can reach the local variable.

        // "this"
        if (name.equals("this")) {
            localVariables.add(new Var(
                    name,
                    desc,
                    index,
                    -1, // "this" is one of method arguments
                    end.getOffset()));
            super.visitLocalVariable(name, desc, sig, start, end, index);
            return;
        }

        // Other local variables
        if (TypeUtil.isTypeDescSupported(desc)) {
            int original = start.getOffset();
            int startOffset = original == 0 ?
                    -1 : // method arguments
                    original - (index <= 3 ? 1 : 2);
            localVariables.add(new Var(
                    name,
                    desc,
                    index,
                    startOffset,
                    end.getOffset()));
        }
        super.visitLocalVariable(name, desc, sig, start, end, index);
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        currOffset = label.getOffset();
    }

    @Override
    public void visitMethodInsn(
            int opcode,
            String owner,
            String name,
            String desc,
            boolean isInterface) {
        if (opcode == Opcodes.INVOKEVIRTUAL
                // TODO: check owner is subclass of Exp?
                && name.equals(Constants.EVAL_METH_NAME)
                && desc.equals(Constants.EVAL_METH_DESC)) {
            // Save current byteoffset
            Data.addToOffsetsOfEvals(fullMethodName, currOffset);
        }
        super.visitMethodInsn(opcode, owner, name, desc, isInterface);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        Data.addToLocalVars(fullMethodName, localVariables);
    }
}
