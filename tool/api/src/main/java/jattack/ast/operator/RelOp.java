package jattack.ast.operator;

import com.microsoft.z3.ArithSort;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

/**
 * Relational operator.
 */
public enum RelOp implements Op {
    EQ("=="),
    GE(">="),
    GT(">"),
    LE("<="),
    LT("<"),
    NE("!=");

    private final String strRep;

    RelOp(String strRep) {
        this.strRep = strRep;
    }

    public boolean apply(Number left, Number right) {
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

    private boolean apply(int left, int right) {
        switch (this) {
        case EQ:
            return left == right;
        case GE:
            return left >= right;
        case GT:
            return left > right;
        case LE:
            return left <= right;
        case LT:
            return left < right;
        case NE:
            return left != right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private boolean apply(long left, long right) {
        switch (this) {
        case EQ:
            return left == right;
        case GE:
            return left >= right;
        case GT:
            return left > right;
        case LE:
            return left <= right;
        case LT:
            return left < right;
        case NE:
            return left != right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private boolean apply(double left, double right) {
        switch (this) {
        case EQ:
            return left == right;
        case GE:
            return left >= right;
        case GT:
            return left > right;
        case LE:
            return left <= right;
        case LT:
            return left < right;
        case NE:
            return left != right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    public <R extends ArithSort> BoolExpr buildZ3Expr(Expr<R> left, Expr<R> right, Context ctx) {
        switch (this) {
        case EQ:
            return ctx.mkEq(left, right);
        case GE:
            return ctx.mkGe(left, right);
        case GT:
            return ctx.mkGt(left, right);
        case LE:
            return ctx.mkLe(left, right);
        case LT:
            return ctx.mkLt(left, right);
        case NE:
            return ctx.mkNot(ctx.mkEq(left, right));
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    @Override
    public String asStr() {
        return strRep;
    }
}
