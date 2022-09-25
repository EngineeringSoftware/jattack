package sketchy.ast.operator;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.ArithSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

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
        // right has the same type with left so we check left only
        if (left instanceof Integer) {
            return apply(left.intValue(), right.intValue());
        } else if (left instanceof Long) {
            return apply(left.longValue(), right.longValue());
        } else if (left instanceof Double) {
            return apply(left.doubleValue(), right.doubleValue());
        } else {
            throw new RuntimeException("Unsupported type: " + left.getClass());
        }
    }

    private int apply(int left, int right) {
        switch (this) {
        case ADD:
            return left + right;
        case SUB:
            return left - right;
        case MUL:
            return left * right;
        case DIV:
            return left / right; // throw ArithmeticException
        case MOD:
            return left % right; // throw ArithmeticException
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private long apply(long left, long right) {
        switch (this) {
        case ADD:
            return left + right;
        case SUB:
            return left - right;
        case MUL:
            return left * right;
        case DIV:
            return left / right; // throw ArithmeticException
        case MOD:
            return left % right; // throw ArithmeticException
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    public double apply(double left, double right) {
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
