package jattack.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import jattack.Constants;
import jattack.data.Data;
import jattack.util.TypeUtil;

import java.util.ArrayDeque;
import java.util.Deque;
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

    /**
     * Stack to keep track of dangling NEW instructions. We pop a
     * marker from the stack when we encounter a INVOKESPEICAL <init>
     * instruction, because we know they create another new object.
     * If the stack is empty, then we know that all NEW
     * instructions have been matched with INVOKESPECIAL <init>, and
     * the next INVOKESPECIAL <init> instruction must be the one that
     * initializes "this".
     */
    private Deque<String> danglingNewStack;

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
        // If not in constrcutor, "this" must have been initialized.
        // Otherwise, we have to wait.
        thisInitialized = !m_isConstructor;
        danglingNewStack = new ArrayDeque<>();
    }


    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (m_isConstructor) {
            // Push a marker to the stack to indicate that a NEW
            // instruction has been encountered.
            if (opcode == Opcodes.NEW) {
                danglingNewStack.push("has_dangling_new");
            }
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(
            int opcode,
            String owner,
            String name,
            String desc,
            boolean isInterface) {
        if (!thisInitialized
                && opcode == Opcodes.INVOKESPECIAL
                && name.equals("<init>")) {
            if (!danglingNewStack.isEmpty()) {
                // the INVOKESPECIAL <init> pairs with the NEW on
                // the stack, so we can pop it.
                danglingNewStack.pop();
            } else {
                // This is the one that instantiates the class we
                // are looking for.
                thisInitialized = true;
            }
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
        Set<Var> availableVars = Data.getAccessibleLocalVars(m_fullMethodName, offset);

        // Save values of variables
        invokeResetMemory();
        saveLocalVarValues(availableVars);
        saveFieldValues();

        // invoke original eval()
        super.visitMethodInsn(opcode, owner, name, desc, isInterface);

        // Update values of variables
        updateLocalVarValues(availableVars);
        updateFieldValues();
    }

    private void saveFieldValues() {
        if (m_isStatic || !thisInitialized) {
            // Get the fields accessible statically
            // when the hole is in a static method or in a constructor
            // where "this" has not been initialized
            super.visitLdcInsn(Type.getObjectType(m_className));
            invokeFindFieldsForClass();
        } else {
            // Get the fields accessible by "this"
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitLdcInsn(Type.getObjectType(m_className));
            invokeFindFieldsForObject();
        }
    }

    private void updateFieldValues() {
        invokeUpdateFieldValues();
    }

    /**
     * Get values of all the local variables reachable
     * at the current execution point from <code>Data.memory</code>
     * and update them.
     */
    private void updateLocalVarValues(Set<Var> availableVars) {
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
            invokeGetFromMemoryValueOfVar(desc);
            if (!TypeUtil.isDescPrimitive(desc)) {
                // type casting for reference types
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
    private void saveLocalVarValues(Set<Var> availableVars) {
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
            invokeAddToMemory(desc);
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
     * {@code Data.resetMemory()};
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
     * {@code Data.getFromMemoryValueOfVar(String)};
     */
    private void invokeGetFromMemoryValueOfVar(String desc) {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.DATA_CLZ_INTERN_NAME,
                Constants.getGetFromMemoryValueOfSymbolMethName(desc),
                Constants.getGetFromMemoryValueOfSymbolMethDesc(desc),
                false);
    }

    /**
     * {@code Data.addToMemory(String,String,<type>);}
     */
    private void invokeAddToMemory(String desc) {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.DATA_CLZ_INTERN_NAME,
                Constants.ADD_TO_MEMORY_METH_NAME,
                Constants.getAddToMemoryMethDesc(desc),
                false);
    }

    /**
     * {@code FieldAnalyzer.findFields(Object,Class<?>);}
     */
    private void invokeFindFieldsForObject() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.FIND_FIELDS_FOR_OBJECT_METH_NAME,
                Constants.FIND_FIELDS_FOR_OBJECT_METH_DESC,
                false);
    }

    /**
     * {@code FieldAnalyzer.findFields(Class<?>);}
     */
    private void invokeFindFieldsForClass() {
        super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                Constants.FIELD_ANALYZER_CLZ_INTERN_NAME,
                Constants.FIND_FIELDS_FOR_CLASS_METH_NAME,
                Constants.FIND_FIELDS_FOR_CLASS_METH_DESC,
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
