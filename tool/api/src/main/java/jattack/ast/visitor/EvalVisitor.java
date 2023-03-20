package jattack.ast.visitor;

import jattack.ast.exp.AssignExp;
import jattack.ast.exp.ByteVal;
import jattack.ast.exp.CastExp;
import jattack.ast.exp.CharVal;
import jattack.ast.exp.Exp;
import jattack.ast.exp.FloatVal;
import jattack.ast.exp.LongVal;
import jattack.ast.exp.PreIncExp;
import jattack.ast.exp.RefId;
import jattack.ast.exp.ShiftExp;
import jattack.ast.exp.DoubleVal;
import jattack.ast.exp.ImBoolVal;
import jattack.ast.exp.ImDoubleVal;
import jattack.ast.exp.IntArrVal;
import jattack.ast.exp.RefArrAccessExp;
import jattack.ast.exp.ShortVal;
import jattack.ast.operator.OpNode;
import jattack.ast.nodetypes.TerminalNode;
import jattack.ast.operator.AriOp;
import jattack.ast.exp.BAriExp;
import jattack.ast.exp.BoolVal;
import jattack.ast.exp.ImIntVal;
import jattack.ast.exp.IntVal;
import jattack.ast.exp.LogExp;
import jattack.ast.exp.RelExp;
import jattack.ast.operator.IncDecOp;
import jattack.ast.operator.ShiftOp;
import jattack.ast.operator.LogOp;
import jattack.ast.operator.RelOp;
import jattack.ast.stmt.BlockStmt;
import jattack.ast.stmt.ExprStmt;
import jattack.ast.stmt.IfStmt;
import jattack.ast.stmt.Stmt;
import jattack.ast.stmt.TryStmt;
import jattack.ast.stmt.WhileStmt;
import jattack.exception.InvocationTemplateException;
import jattack.util.TypeUtil;

import java.util.Stack;

/**
 * Visitor to evaluate a AST node.
 */
public class EvalVisitor extends Visitor {

    // We do not use ArrayDeque as we need to push null onto stack.
    private final Stack<Object> stack = new Stack<>();

    /**
     * Short circuit flag.
     */
    private boolean shortCircuit = false;

    public Object getResult() {
        if (stack.isEmpty()) {
            throw new RuntimeException("EvalVisitor stack is empty!");
        }
        return stack.peek();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(BAriExp<N> node) {
        N right = (N) stack.pop();
        N left = (N) stack.pop();
        AriOp op = node.getOp();
        stack.push(op.apply(left, right));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(ShiftExp<N> node) {
        int right = (int) stack.pop();
        N left = (N) stack.pop();
        ShiftOp op = node.getOp();
        stack.push(op.apply(left, right));
    }

    @Override
    public boolean visit(BoolVal node) {
        if (shortCircuit) {
            return false;
        }
        return super.visit(node);
    }

    @Override
    public void endVisit(BoolVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public boolean visit(ImBoolVal node) {
        if (shortCircuit) {
            return false;
        }
        return super.visit(node);
    }

    @Override
    public void endVisit(ImBoolVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(CharVal node) {
        endVisitTerminalNode(node);
    }

    public void endVisit(ByteVal node) {
        endVisitTerminalNode(node);
    }

    public void endVisit(ShortVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(IntVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(ImIntVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(FloatVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(DoubleVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(ImDoubleVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(LongVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public boolean visit(RefId<?> node) {
        if (node.getType().equals(Boolean.class)
                && shortCircuit) {
            return false;
        }
        return super.visit(node);
    }

    @Override
    public void endVisit(RefId<?> node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(IntArrVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(RefArrAccessExp<?, ?> node) {
        // id
        Object arr = stack.pop();
        // index
        int i = (int) stack.pop();
        stack.push(TypeUtil.arrayGet(arr, i));
    }

    @Override
    public boolean visit(OpNode<?> op) {
        // Can only be in LogExp
        // TODO: move this to endVisit so we don't need to visit op
        //  at all.
        boolean left = (boolean) stack.peek();
        if ((!left && op.getOp() == LogOp.AND)
                || (left && op.getOp() == LogOp.OR)) {
            shortCircuit = true;
        }
        return super.visit(op);
    }

    @Override
    public boolean visit(LogExp node) {
        if (shortCircuit) {
            return false;
        }
        return super.visit(node);
    }

    @Override
    public void endVisit(LogExp node) {
        if (shortCircuit) {
            shortCircuit = false;
            return;
        }
        boolean right = (boolean) stack.pop();
        boolean left = (boolean) stack.pop();
        stack.push(node.getOp().apply(left, right));
    }

    @Override
    public <N extends Number> boolean visit(RelExp<N> node) {
        if (shortCircuit) {
            return false;
        }
        return super.visit(node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(RelExp<N> node) {
        N right = (N) stack.pop();
        N left = (N) stack.pop();
        RelOp op = node.getOp();
        stack.push(op.apply(left, right));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(PreIncExp<N> node) {
        N val = (N) stack.pop();
        N newVal = (N) IncDecOp.PRE_INC.apply(val);
        node.updateVal(newVal); // side effect: update value in memory
        stack.push(newVal);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void endVisit(AssignExp<T> node) {
        stack.pop(); // target
        // no need to pop since we still have to push back anyway
        node.updateVal((T) stack.peek());
    }

    @Override
    public <T> void endVisit(CastExp<T> node) {
        Object srcVal = stack.pop();
        Class<T> type = node.getType();
        stack.push(cast(srcVal, type));
    }

    private Object cast(Object srcVal, Class<?> type)
            throws InvocationTemplateException{
        try {
            return cast0(srcVal, type);
        } catch (ClassCastException e) {
            throw new InvocationTemplateException(e);
        }
    }

    private Object cast0(Object srcVal, Class<?> type)
            throws ClassCastException {
        if (!TypeUtil.isBoxed(type)) {
            return type.cast(srcVal);
        }
        Class<?> srcType = srcVal.getClass();
        String castingErrMsg = "Wrong use of cast API: cannot cast " + srcVal.getClass() + " to " + type;
        if (type.equals(Byte.class)) {
            byte tgtVal;
            if (TypeUtil.isNumberBoxed(srcType)) {
                tgtVal = ((Number) srcVal).byteValue();
            } else if (srcVal instanceof Character) {
                tgtVal = (byte) ((Character) srcVal).charValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Short.class)) {
            short tgtVal;
            if (TypeUtil.isNumberBoxed(srcType)) {
                tgtVal = ((Number) srcVal).shortValue();
            } else if (srcVal instanceof Character) {
                tgtVal = (short) ((Character) srcVal).charValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Character.class)) {
            char tgtVal;
            if (srcType.equals(Byte.class)) {
                tgtVal = (char) ((Byte) srcVal).byteValue();
            } else if (srcType.equals(Short.class)) {
                tgtVal = (char) ((Short) srcVal).shortValue();
            } else if (srcType.equals(Character.class)) {
                tgtVal = (Character) srcVal;
            } else if (srcType.equals(Integer.class)) {
                tgtVal = (char) ((Integer) srcVal).intValue();
            } else if (srcType.equals(Long.class)) {
                tgtVal = (char) ((Long) srcVal).longValue();
            } else if (srcType.equals(Float.class)) {
                tgtVal = (char) ((Float) srcVal).floatValue();
            } else if (srcType.equals(Double.class)) {
                tgtVal = (char) ((Double) srcVal).doubleValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Integer.class)) {
            int tgtVal;
            if (TypeUtil.isNumberBoxed(srcType)) {
                tgtVal = ((Number) srcVal).intValue();
            } else if (srcVal instanceof Character) {
                tgtVal = ((Character) srcVal).charValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Long.class)) {
            long tgtVal;
            if (TypeUtil.isNumberBoxed(srcType)) {
                tgtVal = ((Number) srcVal).longValue();
            } else if (srcVal instanceof Character) {
                tgtVal = ((Character) srcVal).charValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Float.class)) {
            float tgtVal;
            if (TypeUtil.isNumberBoxed(srcType)) {
                tgtVal = ((Number) srcVal).floatValue();
            } else if (srcVal instanceof Character) {
                tgtVal = ((Character) srcVal).charValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Double.class)) {
            double tgtVal;
            if (TypeUtil.isNumberBoxed(srcType)) {
                tgtVal = ((Number) srcVal).doubleValue();
            } else if (srcVal instanceof Character) {
                tgtVal = ((Character) srcVal).charValue();
            } else {
                throw new RuntimeException(castingErrMsg);
            }
            return tgtVal;
        } else if (type.equals(Boolean.class)) {
            if (srcVal instanceof Boolean) {
                return srcVal;
            }
            throw new RuntimeException(castingErrMsg);
        } else {
            throw new RuntimeException("Unexpected boxed type: " + type);
        }
    }

    @Override
    public void visitStmt(ExprStmt node) {
        node.getExpression().accept(this);
        stack.pop();
        assertStackIsEmpty();
    }

    @Override
    public void visitStmt(IfStmt node) {
        node.getCondition().accept(this);
        boolean cond = (boolean) stack.pop();
        assertStackIsEmpty();
        if (cond) {
            node.getThenStmt().accept(this);
        } else if (node.hasElseBranch()) {
            node.getElseStmt().accept(this);
        }
    }

    @Override
    public void visitStmt(WhileStmt node) {
        while (visitCondAndEval(node.getCondition())) {
            node.getBody().accept(this);
            visitCondAndEval(node.getCondition());
        }
    }

    @Override
    public void visitStmt(BlockStmt node) {
        for (Stmt s : node.getStmts()) {
            s.accept(this);
        }
    }

    @Override
    public <T extends Throwable> void visitStmt(TryStmt<T> node) {
        try {
            node.getTryBlock().accept(this);
        } catch (InvocationTemplateException e) {
            // unwrap
            Throwable cause = e.getCause();
            if (node.getExceptionType().isAssignableFrom(cause.getClass())) {
                node.getCatchBlock().accept(this);
            } else {
                throw e;
            }
        } catch (Throwable e) {
            if (node.getExceptionType().isAssignableFrom(e.getClass())) {
                node.getCatchBlock().accept(this);
            } else {
                throw e;
            }
        } finally {
            if (node.hasFinally()) {
                node.getFinallyBlock().accept(this);
            }
        }
    }

    private boolean visitCondAndEval(Exp<Boolean> cond) {
        cond.accept(this);
        boolean ret = (boolean) stack.pop();
        assertStackIsEmpty();
        return ret;
    }

    private void assertStackIsEmpty() {
        if (!stack.isEmpty()) {
            throw new RuntimeException("Stack should be empty!");
        }
    }

    private void endVisitTerminalNode(TerminalNode<?> node) {
        stack.push(node.getVal());
    }
}
