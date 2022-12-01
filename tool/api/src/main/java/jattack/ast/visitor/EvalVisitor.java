package jattack.ast.visitor;

import jattack.ast.exp.AssignExp;
import jattack.ast.exp.ByteVal;
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

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Visitor to evaluate a AST node.
 */
public class EvalVisitor extends Visitor {

    private final Deque<Object> stack = new ArrayDeque<>();

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
        int right = castToInteger(stack.pop());
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
        int i = castToInteger(stack.pop());
        stack.push(Array.get(arr, i));
    }

    @Override
    public boolean visit(OpNode<?> op) {
        // Can only be in LogExp
        // TODO: move this to endVisit so we don't need to visit op
        //  at all.
        boolean left = cast(stack.peek(), Boolean.class);
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
        boolean right = castToBoolean(stack.pop());
        boolean left = castToBoolean(stack.pop());
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

    /* Helper methods. */

    private static Boolean castToBoolean(Object value) {
        return cast(value, Boolean.class);
    }

    private static Integer castToInteger(Object value) {
        return cast(value, Integer.class);
    }

    private static <T> T cast(Object value, Class<T> type) {
        validateCast(value, type);
        return type.cast(value);
    }

    private static void validateCast(Object value, Class<?> type) {
        if (!type.isInstance(value)) {
            throw new RuntimeException(value + " must be subtype of " + type);
        }
    }
}
