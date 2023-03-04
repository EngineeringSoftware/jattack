package jattack.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import jattack.Constants;
import jattack.data.Data;
import jattack.util.TypeUtil;

/**
 * MethodVisitor to get available local variables at every eval()
 * method.
 */
public class StaticGenMethodVisitor extends MethodVisitor {

    private final MethodNode mn;
    private final MethodVisitor next;
    private final String fullMethodName;

    public StaticGenMethodVisitor(
            MethodVisitor mv,
            String fullMethodName,
            int access,
            String name,
            String desc,
            String signature,
            String[] exceptions) {
        this(new MethodNode(access, name, desc, signature, exceptions), mv, fullMethodName);
    }

    private StaticGenMethodVisitor(MethodNode mn, MethodVisitor mv, String fullMethodName) {
        super(Constants.ASM_VERSION, mn);
        this.mn = mn;
        this.next = mv;
        this.fullMethodName = fullMethodName;
    }

    @Override
    public void visitEnd() {
        mn.accept(next);
        getAvailableLocalVars();
    }

    private void getAvailableLocalVars() {
        getAvailLocalVarsForEachHole();
    }

    /**
     * Parse instruction list to get the hole id and offset of every
     * eval().
     */
    private void getAvailLocalVarsForEachHole() {
        InsnList insns = mn.instructions;
        int offset = -1;
        for (AbstractInsnNode insn : insns) {
            if (insn.getType() == AbstractInsnNode.LABEL) {
                offset = ((LabelNode) insn).getLabel().getOffset();
                continue;
            }
            if (!isEval(insn)) {
                continue;
            }
            int holeId = getHoleId(insn);
            Data.addToHolesByMethod(fullMethodName, holeId);
            saveLocalVarsAvialHere(holeId, offset);
        }
    }

    private void saveLocalVarsAvialHere(int holeId, int offset) {
        // See which variables are available at this point
        for (LocalVariableNode lvn : mn.localVariables) {
            saveLocalVarIfItIsAvailAtOffset(lvn, holeId, offset);
        }
    }

    private int getHoleId(AbstractInsnNode evalInsn) {
        // Find the instruction to load int argument for eval
        AbstractInsnNode loadIntInsn = evalInsn.getPrevious();
        while (!isLoadIntCon(loadIntInsn)) {
            loadIntInsn = loadIntInsn.getPrevious();
        }
        // Get hole id from the argument
        return getIntVal(loadIntInsn);
    }

    private void saveLocalVarIfItIsAvailAtOffset(
            LocalVariableNode lvn, int holeId, int offset) {
        String name = lvn.name;
        // "this"
        if (name.equals("this")
                || lvn.start.getLabel().getOffset() == 0) {
            // method arguments
            saveLocalVarAtHole(holeId, name, lvn.desc);
            return;
        }
        // Other local variables
        int startOffset = lvn.start.getLabel().getOffset()
                - (lvn.index <= 3 ? 1 : 2);
        int endOffset = lvn.end.getLabel().getOffset();
        if (startOffset > offset || offset >= endOffset) {
            // not in scope
            return;
        }
        // In scope we should save this local variable
        saveLocalVarAtHole(holeId, name, lvn.desc);
    }

    private void saveLocalVarAtHole(int holeId, String name, String desc) {
        if (!TypeUtil.isTypeDescSupported(desc)) {
            // We do not save any local variables with type not
            // supported yet.
            return;
        }
        if (TypeUtil.isDescPrimitive(desc)) {
            // Box primitives
            desc = TypeUtil.primitiveDescToBoxedDesc(desc);
        }
        Data.addToSymbolsByHole(holeId, new Symbol(name, desc));
    }

    /**
     * Get integer value from the given load-int instruction.
     * <p>
     * Precondition: {@code isLoadIntCon(insn)} is {@code true}.
     */
    private int getIntVal(AbstractInsnNode insn) {
        int type = insn.getType();
        if (type == AbstractInsnNode.INSN) {
            switch (insn.getOpcode()) {
            case Opcodes.ICONST_M1:
                // not reached since hole id starting from 1
                return -1;
            case Opcodes.ICONST_0:
                // not reached since hole id starting from 1
                return 0;
            case Opcodes.ICONST_1:
                return 1;
            case Opcodes.ICONST_2:
                return 2;
            case Opcodes.ICONST_3:
                return 3;
            case Opcodes.ICONST_4:
                return 4;
            case Opcodes.ICONST_5:
                return 5;
            }
        }
        if (type == AbstractInsnNode.INT_INSN) {
            return ((IntInsnNode) insn).operand;
        }
        // Must be LDC insn
        return (int) ((LdcInsnNode) insn).cst;
    }

    /**
     * Return the given instruction is one of the all possible
     * instructions of pushing an int constant onto the operand stack.
     */
    private boolean isLoadIntCon(AbstractInsnNode insn) {
        int type = insn.getType();
        if (type == AbstractInsnNode.INSN) {
            int opcode = insn.getOpcode();
            return opcode == Opcodes.ICONST_M1
                    || opcode == Opcodes.ICONST_0
                    || opcode == Opcodes.ICONST_1
                    || opcode == Opcodes.ICONST_2
                    || opcode == Opcodes.ICONST_3
                    || opcode == Opcodes.ICONST_4
                    || opcode == Opcodes.ICONST_5;
        }
        if (type == AbstractInsnNode.INT_INSN) {
            return insn.getOpcode() == Opcodes.BIPUSH
                    || insn.getOpcode() == Opcodes.SIPUSH;
        }
        return type == AbstractInsnNode.LDC_INSN;
    }

    /**
     * Match eval(I) method call.
     */
    private boolean isEval(AbstractInsnNode insn) {
        if (insn.getType() != AbstractInsnNode.METHOD_INSN) {
            return false;
        }
        MethodInsnNode methodInsn = (MethodInsnNode) insn;
        // TODO: what if there are other eval(I) methods besides ours.
        return methodInsn.name.equals(Constants.EVAL_METH_NAME)
                && methodInsn.desc.equals(Constants.EVAL_METH_DESC);
    }
}
