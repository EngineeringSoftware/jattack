package jattack.ast.visitor;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPSort;
import com.microsoft.z3.Sort;
import jattack.ast.exp.AssignExp;
import jattack.ast.exp.BAriExp;
import jattack.ast.exp.ByteVal;
import jattack.ast.exp.CharVal;
import jattack.ast.exp.FloatVal;
import jattack.ast.exp.LongVal;
import jattack.ast.exp.PreIncExp;
import jattack.ast.exp.RefId;
import jattack.ast.exp.ShiftExp;
import jattack.ast.exp.BoolId;
import jattack.ast.exp.BoolVal;
import jattack.ast.exp.DoubleId;
import jattack.ast.exp.DoubleVal;
import jattack.ast.exp.ImBoolVal;
import jattack.ast.exp.ImDoubleVal;
import jattack.ast.exp.ImIntVal;
import jattack.ast.exp.IntId;
import jattack.ast.exp.IntVal;
import jattack.ast.exp.LogExp;
import jattack.ast.exp.RefArrAccessExp;
import jattack.ast.exp.RelExp;
import jattack.ast.exp.ShortVal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Visitor to build an {@link com.microsoft.z3.Expr} from a AST node
 * that Z3 solver can solve.
 */
public class Z3ExprBuilder extends Visitor {

    private final Context ctx = new Context();
    private final Deque<Expr<? extends Sort>> stack = new ArrayDeque<>();
    private boolean buildable = true;

    // double sort
    private final FPSort DOUBLE_SORT = ctx.mkFPSortDouble();

    // TODO: a better way to handle non-buildable?

    public Expr<? extends Sort> getZ3Expr() {
        if (!buildable) {
            return null;
        }
        if (stack.isEmpty()) {
            throw new RuntimeException("Z3ExprBuilder stack is empty!");
        }
        return stack.peek();
    }

    public Context getContext() {
        return ctx;
    }

    @Override
    public boolean visit(BoolId node) {
        return buildable;
    }

    @Override
    public void endVisit(BoolId node) {
        stack.push(ctx.mkBoolConst(node.asStr()));
    }

    @Override
    public boolean visit(BoolVal node) {
        return buildable;
    }

    @Override
    public void endVisit(BoolVal node) {
        stack.push(ctx.mkBool(node.getVal()));
    }

    @Override
    public boolean visit(ImBoolVal node) {
        return buildable;
    }

    @Override
    public void endVisit(ImBoolVal node) {
        stack.push(ctx.mkBool(node.getVal()));
    }

    @Override
    public boolean visit(IntId node) {
        return buildable;
    }

    @Override
    public void endVisit(IntId node) {
        stack.push(ctx.mkIntConst(node.asStr()));
    }

    @Override
    public boolean visit(RefId<?> node) {
        return buildable;
    }

    public void endVisit(RefId<?> node) {
        // Skip float point numbers because for example,
        // x == x is not always true when x is NaN.
        Class<?> type = node.getType();
        if (type.equals(double[].class)
                || type.equals(Double.class)
                || type.equals(float[].class)
                || type.equals(float.class)) {
            buildable = false;
            return;
        }
        // Do for other types
        stack.push(ctx.mkIntConst(node.getJavaStr())); // abuse int
    }

    @Override
    public boolean visit(RefArrAccessExp<?, ?> node) {
        return buildable;
    }

    @Override
    public void endVisit(RefArrAccessExp<?, ?> node) {
        if (!buildable) {
            return;
        }
        // We do not introduce the theory of arrays; instead we treat
        // a array access expression as an independent variable.
        // TODO: perhaps we want to introduce the theory of arrays
        //  in the future.
        stack.pop(); // id
        stack.pop(); // index
        stack.push(ctx.mkIntConst(node.getJavaStr())); // abuse int
    }

    @Override
    public <N extends Number> boolean visit(BAriExp<N> node) {
        return buildable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(BAriExp<N> node) {
        if (!buildable) {
            return;
        }
        Expr right = stack.pop();
        Expr left = stack.pop();
        Expr comb = node.getOp().buildZ3Expr(left, right, ctx);
        if (comb != null) {
            stack.push(comb);
        } else {
            buildable = false;
        }
    }

    @Override
    public <N extends Number> boolean visit(ShiftExp<N> node) {
        return buildable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(ShiftExp<N> node) {
        if (!buildable) {
            return;
        }
        Expr right = stack.pop();
        Expr left = stack.pop();
        Expr comb = node.getOp().buildZ3Expr(left, right, ctx);
        if (comb != null) {
            stack.push(comb);
        } else {
            buildable = false;
        }
    }

    @Override
    public boolean visit(CharVal node) {
        return buildable;
    }

    @Override
    public void endVisit(CharVal node) {
        // TODO: I guess we cannot encode a char in Z3?
        buildable = false;
    }

    @Override
    public boolean visit(ByteVal node) {
        return buildable;
    }

    @Override
    public void endVisit(ByteVal node) {
        stack.push(ctx.mkInt(node.getVal()));
    }

    @Override
    public boolean visit(ShortVal node) {
        return buildable;
    }

    @Override
    public void endVisit(ShortVal node) {
        stack.push(ctx.mkInt(node.getVal()));
    }

    @Override
    public boolean visit(IntVal node) {
        return buildable;
    }

    @Override
    public void endVisit(IntVal node) {
        stack.push(ctx.mkInt(node.getVal()));
    }

    @Override
    public boolean visit(ImIntVal node) {
        return buildable;
    }

    @Override
    public void endVisit(ImIntVal node) {
        stack.push(ctx.mkInt(node.getVal()));
    }

    @Override
    public boolean visit(LongVal node) {
        return buildable;
    }

    @Override
    public void endVisit(LongVal node) {
        stack.push(ctx.mkInt(node.getVal()));
    }

    @Override
    public boolean visit(DoubleId node) {
        return buildable;
    }

    @Override
    public void endVisit(DoubleId node) {
        // Skip float point numbers because for example,
        // x == x is not always true when x is NaN.
        buildable = false;
    }

    @Override
    public boolean visit(FloatVal node) {
        return buildable;
    }

    @Override
    public void endVisit(FloatVal node) {
        // Skip float point numbers because for example,
        // x == x is not always true when x is NaN.
        buildable = false;
    }

    @Override
    public boolean visit(DoubleVal node) {
        return buildable;
    }

    @Override
    public void endVisit(DoubleVal node) {
        // Skip float point numbers because for example,
        // x == x is not always true when x is NaN.
        buildable = false;
    }

    @Override
    public boolean visit(ImDoubleVal node) {
        return buildable;
    }

    @Override
    public void endVisit(ImDoubleVal node) {
        // Skip float point numbers because for example,
        // x == x is not always true when x is NaN.
        buildable = false;
    }

    @Override
    public <N extends Number> boolean visit(RelExp<N> node) {
        return buildable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends Number> void endVisit(RelExp<N> node) {
        if (!buildable) {
            return;
        }
        Expr right = stack.pop();
        Expr left = stack.pop();
        stack.push(node.getOp().buildZ3Expr(left, right, ctx));
    }

    @Override
    public boolean visit(LogExp node) {
        return buildable;
    }

    @Override
    public void endVisit(LogExp node) {
        if (!buildable) {
            return;
        }
        BoolExpr right = (BoolExpr) stack.pop();
        BoolExpr left = (BoolExpr) stack.pop();
        stack.push(node.getOp().buildZ3Expr(left, right, ctx));
    }

    @Override
    public <N extends Number> boolean visit(PreIncExp<N> node) {
        return buildable;
    }

    @Override
    public <N extends Number> void endVisit(PreIncExp<N> node) {
        // TODO: encode with Z3 solver
        buildable = false;
    }

    @Override
    public <T> boolean visit(AssignExp<T> node) {
        return buildable;
    }

    @Override
    public <T> void endVisit(AssignExp<T> node) {
        buildable = false;
    }
}
