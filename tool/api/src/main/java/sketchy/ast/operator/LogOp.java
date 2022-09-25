package sketchy.ast.operator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

/**
 * Logical Operator.
 */
public enum LogOp implements Op {
    AND("&&"),
    OR("||");

    private final String strRep;

    LogOp(String strRep) {
        this.strRep = strRep;
    }

    @Override
    public String asStr() {
        return strRep;
    }

    public boolean apply(boolean left, boolean right) {
        switch (this) {
        case AND:
            return left && right;
        case OR:
            return left || right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    @SuppressWarnings("unchecked")
    public BoolExpr buildZ3Expr(BoolExpr left, BoolExpr right, Context ctx) {
        switch (this) {
        case AND:
            return ctx.mkAnd(left, right);
        case OR:
            return ctx.mkOr(left, right);
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }
}
