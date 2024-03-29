package jattack.ast.operator;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.ArithSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import jattack.data.Data;

/**
 * Arithmetic operator.
 */
public enum AriOp implements AriOrShiftOp {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%");

    private final String strRep;

    AriOp(String strRep) {
        this.strRep = strRep;
    }

    @Override
    public String asStr() {
        return strRep;
    }

    public Number apply(Number left, Number right) {
        try {
            return apply0(left, right);
        } catch (ArithmeticException e) {
            // Document the exception, so we can know we should not
            // crash jattack when InvocationTargetException is thrown
            // with this exception as the cause.
            Data.saveInvocationTemplateException(e);
            throw e;
        }
    }

    private Number apply0(Number left, Number right) throws ArithmeticException {
        // right has the same type with left so we check left only
        if (left instanceof Integer) {
            return apply0(left.intValue(), right.intValue());
        } else if (left instanceof Long) {
            return apply0(left.longValue(), right.longValue());
        } else if (left instanceof Float) {
            return apply0(left.floatValue(), right.floatValue());
        } else if (left instanceof Double) {
            return apply0(left.doubleValue(), right.doubleValue());
        } else {
            throw new RuntimeException("Unsupported type: " + left.getClass());
        }
    }

    private int apply0(int left, int right) throws ArithmeticException {
        switch (this) {
        case ADD:
            return left + right;
        case SUB:
            return left - right;
        case MUL:
            return left * right;
        case DIV:
            return left / right;
        case MOD:
            return left % right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private long apply0(long left, long right) throws ArithmeticException {
        switch (this) {
        case ADD:
            return left + right;
        case SUB:
            return left - right;
        case MUL:
            return left * right;
        case DIV:
            return left / right;
        case MOD:
            return left % right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    public float apply0(float left, float right) throws ArithmeticException {
        switch (this) {
        case ADD:
            return left + right;
        case SUB:
            return left - right;
        case MUL:
            return left * right;
        case DIV:
            return left / right;
        case MOD:
            return left % right;
        default:
            throw new RuntimeException("Unsupported operator " + this.asStr());
        }
    }

    public double apply0(double left, double right) throws ArithmeticException {
        switch (this) {
        case ADD:
            return left + right;
        case SUB:
            return left - right;
        case MUL:
            return left * right;
        case DIV:
            return left / right;
        case MOD:
            return left % right;
        default:
            throw new RuntimeException("Unsupported operator " + this.asStr());
        }
    }

    @SuppressWarnings("unchecked")
    public <R extends ArithSort> ArithExpr<R> buildZ3Expr(Expr<R> left, Expr<R> right, Context ctx) {
        switch (this) {
        case ADD:
            return ctx.mkAdd(left, right);
        case SUB:
            return ctx.mkSub(left, right);
        case MUL:
            return ctx.mkMul(left, right);
        case DIV:
            return ctx.mkDiv(left, right);
        // TODO: build these operators in Z3.
        case MOD:
            // returning null means we will not build a Z3 expr.
            return null;
        default:
            throw new RuntimeException("Unsupported operator " + this.asStr());
        }
    }
}
