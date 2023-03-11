package jattack.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import jattack.Constants;
import jattack.data.Data;
import jattack.util.TypeUtil;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * MethodVisitor to insert instructions to save values of local
 * variables right before each {@code eval()}.
 */
public class SaveLocalVarValuesMethodVisitor extends MethodVisitor {

    private final String m_className;
    private final String m_fullMethodName;
    private final Deque<Integer> m_offsets;
    private final boolean m_isStatic;
    private final boolean m_isConstructor;

    /**
     * Mark if "this" has been initialized, which is not done before
     * invoksepcial is finished.
     */
    private boolean thisInitialized;

    public SaveLocalVarValuesMethodVisitor(
            MethodVisitor mv,
            String className,
            String fullMethodName,
            boolean isStatic,
            boolean isConstructor) {
        super(Constants.ASM_VERSION, mv);
        m_className = className;
        m_fullMethodName = fullMethodName;
        m_offsets = Data.getOffsetsOfEvalsOfMethod(m_fullMethodName);
        m_isStatic = isStatic;
        m_isConstructor = isConstructor;
        if (!m_isConstructor) {
            // If not in constrcutor, "this" must have been
            // initialized.
            thisInitialized = true;
        }
    }

    @Override
    public void visitMethodInsn(
            int opcode,
            String owner,
            String name,
            String desc,
            boolean isInterface) {
        // Detect the first invokespecial in the constructor, which
        // should be the one that initializes the instance
        if (!thisInitialized
                && opcode == Opcodes.INVOKESPECIAL
                && name.equals("<init>")) {
            thisInitialized = true;
        }

        // Match eval(I)
        // TODO: what if there are other eval(I) except ours?
        if (opcode == Opcodes.INVOKEVIRTUAL
                // && owner.equals(Constants.EXP_CLZ_INTERN_NAME)
                && name.equals(Constants.EVAL_METH_NAME)
                && desc.equals(Constants.EVAL_METH_DESC)) {
            wrapEval(opcode, owner, name, desc, isInterface);
            return;
        }
        super.visitMethodInsn(opcode, owner, name, desc, isInterface);
    }

    private void wrapEval(
            int opcode,
            String owner,
            String name,
            String desc,
            boolean isInterface) {
        if (m_offsets.isEmpty()) {
            throw new RuntimeException("Queue of evals is empty!");
        }
        int offset = m_offsets.poll();
        // TODO: improve search efficiency with ordered data
        //  structure.
        Set<Var> availableVars = new HashSet<>();
        for (Var var : Data.getLocalVarsOfMethod(m_fullMethodName)) {
            if (var.isReachableAt(offset)) {
                availableVars.add(var);
            }
        }

        // Save values of variables
        invokeResetMemory();
        saveLocalVarValues(availableVars, offset);
        saveFieldValues();

        // invoke original eval()
        super.visitMethodInsn(opcode, owner, name, desc, isInterface);

        // Update values of variables
        updateLocalVarValues(availableVars, offset);
        updateFieldValues();
        invokeResetMemory(); // Release references to help GC
    }

    private void saveFieldValues() {
        invokeInitFieldAnalyzer();
        if (m_isStatic) {
            // Get the fields accessible from the static method
            super.visitLdcInsn(Type.getObjectType(m_className));
            invokeFindFieldsForClass();
        } else {
            // not do if "this" has not been initialized
            if (thisInitialized) {
                // Get the fields accessible by "this"
                super.visitVarInsn(Opcodes.ALOAD, 0);
                invokeFindFieldsForObject();
            }
        }
        invokeSaveFieldValues();
    }

    private void updateFieldValues() {
        invokeUpdateFieldValues();
    }

    /**
     * Get values of all the local variables reachable
     * at the current execution point from <code>Data.memory</code>
     * and update them.
     */
    private void updateLocalVarValues(Set<Var> availableVars, int offset) {
        for (Var var : availableVars) {
            if (var.getName().equals("this")) {
                // we cannot update "this"
                continue;
            }
            String desc = var.getDesc();
            if (!TypeUtil.isTypeDescSupported(desc)) {
                throw new RuntimeException("We should not have collected a local variables of this type: " + desc);
            }
            /*
             * Code we are effectively inserting here:
             * value = Data.getFromMemoryValueOfVar(name);
             * CHECKCAST type: objectref -> objectref
             * XSTORE index: value -> [empty]
             */
            super.visitLdcInsn(var.getName());
            invokeGetFromMemoryValueOfVar();
            // type casting
            if (TypeUtil.isDescPrimitive(desc)) {
                // cast to boxed types and then unbox
                super.visitTypeInsn(Opcodes.CHECKCAST, TypeUtil.primitiveDescToBoxedInternName(desc));
                invokeUnboxing(desc);
            } else {
                super.visitTypeInsn(Opcodes.CHECKCAST, TypeUtil.desc2Intern(desc));
            }
            storeLocalVar(desc, var.getIndex());
        }
    }

    /**
     * Get values of all the local variables reachable
     * at the current execution point and put them in
     * <code>Data.memory</code>.
     */
    private void saveLocalVarValues(Set<Var> availableVars, int offset) {
        for (Var var : availableVars) {
            if (!thisInitialized && var.getName().equals("this")) {
                // cannot save "this" as it has not been initialized
                continue;
            }
            String desc = var.getDesc();
            if (!TypeUtil.isTypeDescSupported(desc)) {
                throw new RuntimeException("We should not have collected a local variables of this type: " + desc);
            }
            /*
             * Code we are effectively inserting here:
             * XLOAD index: [empty] -> value
             * Data.addToMemory(name, desc, value);
             */
            super.visitLdcInsn(var.getName());
            super.visitLdcInsn(desc);
            loadLocalVar(desc, var.getIndex());
            if (TypeUtil.isDescPrimitive(desc)) {
                invokeBoxing(desc); // boxing
            }
            invokeAddToMemory();
        }
    }

    private void loadLocalVar(String desc, int index) {
        loadOrStoreLocalVar(desc, index, true);
    }

    private void storeLocalVar(String desc, int index) {
        loadOrStoreLocalVar(desc, index, false);
    }

    private void loadOrStoreLocalVar(String desc, int index, boolean loadOrStore) {
        if (desc.startsWith("[")
                || desc.startsWith("L")) {
            super.visitVarInsn(loadOrStore ? Opcodes.ALOAD : Opcodes.ASTORE, index);
        } else if (desc.equals("I")
                || desc.equals("Z")
                || desc.equals("B")
                || desc.equals("S")
                || desc.equals("C")) {
            super.visitVarInsn(loadOrStore ? Opcodes.ILOAD : Opcodes.ISTORE, index);
        } else if (desc.equals("F")) {
            super.visitVarInsn(loadOrStore ? Opcodes.FLOAD : Opcodes.FSTORE, index);
        } else if (desc.equals("J")) {
            super.visitVarInsn(loadOrStore ? Opcodes.LLOAD : Opcodes.LSTORE, index);
        } else if (desc.equals("D")) {
            super.visitVarInsn(loadOrStore ? Opcodes.DLOAD : Opcodes.DSTORE, index);
        } else {
            throw new RuntimeException("Unexpected type: " + desc);
        }
    }

    /**
     * Invoke the corresponding {@code valueOf()} method for the given
     * primitive type.
     * @param primitiveDesc the descriptor of the given primitive type
     */
    private void invokeUnboxing(String primitiveDesc) {
        if (!TypeUtil.isDescPrimitive(primitiveDesc)) {
            throw new RuntimeException(primitiveDesc + " is not a primitive type!");
        }
        String primitiveName = TypeUtil.primitiveDescToName(primitiveDesc);
        String boxedInternName = TypeUtil.primitiveDescToBoxedInternName(primitiveDesc);
        super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                boxedInternName,
                primitiveName + "Value", // e.g. intValue
                "()" + primitiveDesc,
                false); // boxing
    }

    /**
     * Invoke the corresponding {@code valueOf()} method for the given
     * primitive type.
     * @param primitiveDesc the descriptor of the given primitive type
     */
    private void invokeBoxing(String primitiveDesc) {
        if (!TypeUtil.isDescPrimitive(primitiveDesc)) {
            throw new RuntimeException(primitiveDesc + " is not a primitive type!");
        }
        String boxedInternName = TypeUtil.primitiveDescToBoxedInternName(primitiveDesc);
        String boxedDesc = TypeUtil.primitiveDescToBoxedDesc(primitiveDesc);
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                boxedInternName,
                "valueOf",
                String.format("(%s)%s", primitiveDesc, boxedDesc),
                false); // boxing
    }

    /**
     * Data.resetMemory();
     */
    private void invokeResetMemory() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.DATA_CLZ_INTERN_NAME,
                Constants.RESET_MEMORY_METH_NAME,
                Constants.RESET_MEMORY_METH_DESC,
                false);
    }

    /**
     * Data.getFromMemoryValueOfVar(String);
     */
    private void invokeGetFromMemoryValueOfVar() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.DATA_CLZ_INTERN_NAME,
                Constants.GET_FROM_MEMORY_VALUE_OF_SYMBOL_METH_NAME,
                Constants.GET_FROM_MEMORY_VALUE_OF_SYMBOL_METH_DESC,
                false);
    }

    /**
     * {@code Data.addToMemory(String, Object);}
     */
    private void invokeAddToMemory() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.DATA_CLZ_INTERN_NAME,
                Constants.ADD_TO_MEMORY_METH_NAME,
                Constants.ADD_TO_MEMORY_METH_DESC,
                false);
    }

    /**
     * {@code FieldAnalyzer.initFieldAnalyzer();}
     */
    private void invokeInitFieldAnalyzer() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.INIT_FIELD_ANALYZER_METH_NAME,
                Constants.INIT_FIELD_ANALYZER_METH_DESC,
                false);
    }

    /**
     * {@code FieldAnalyzer.findFields(Object);}
     */
    private void invokeFindFieldsForObject() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.FIND_FIELDS_METH_NAME,
                Constants.FIND_FIELDS_METH_DESC1,
                false);
    }

    /**
     * {@code FieldAnalyzer.findFields(Class<?>);}
     */
    private void invokeFindFieldsForClass() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.FIND_FIELDS_METH_NAME,
                Constants.FIND_FIELDS_METH_DESC2,
                false);
    }

    /**
     * {@code FieldAnalyzer.saveFieldValues();}
     */
    private void invokeSaveFieldValues() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.SAVE_FIELD_VALUES_METH_NAME,
                Constants.SAVE_FIELD_VALUES_METH_DESC,
                false);
    }

    /**
     * {@code FieldAnalyzer.updateFieldValues();}
     */
    private void invokeUpdateFieldValues() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.UPDATE_FIELD_VALUES_METH_NAME,
                Constants.UPDATE_FIELD_VALUES_METH_DESC,
                false);
    }

    private void pushIntOntoStack(int number) {
        if (number >= Byte.MIN_VALUE && number <= Byte.MAX_VALUE) {
            super.visitIntInsn(Opcodes.BIPUSH, number);
        } else if (number >= Short.MIN_VALUE && number <= Short.MAX_VALUE) {
            super.visitIntInsn(Opcodes.SIPUSH, number);
        } else {
            super.visitLdcInsn(number);
        }
    }
}
