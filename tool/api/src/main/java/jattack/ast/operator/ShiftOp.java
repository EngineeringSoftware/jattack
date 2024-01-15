package jattack.ast.operator;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;

public enum ShiftOp implements AriOrShiftOp {
    SHIFTL("<<"),
    SHIFTR(">>"),
    USHIFTR(">>>");

    private final String strRep;

    ShiftOp(String strRep) {
        this.strRep = strRep;
    }

    @Override
    public String asStr() {
        return strRep;
    }

    public Number apply(Number left, Number right) {
        if (left instanceof Integer && right instanceof Integer) {
            return apply(left.intValue(), right.intValue());
        } else if (left instanceof Integer && right instanceof Long) {
            return apply(left.intValue(), right.longValue());
        } else if (left instanceof Long && right instanceof Integer) {
            return apply(left.longValue(), right.intValue());
        } else if (left instanceof Long && right instanceof Long) {
            return apply(left.longValue(), right.longValue());
        } else {
            throw new RuntimeException(
                    "Unsupported type(s): left " + left.getClass() +
                    "and/or right " + right.getClass());
        }
    }

    private int apply(int left, int right) {
        switch (this) {
        case SHIFTL:
            return left << right;
        case SHIFTR:
            return left >> right;
        case USHIFTR:
            return left >>> right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private int apply(int left, long right) {
        switch (this) {
        case SHIFTL:
            return left << right;
        case SHIFTR:
            return left >> right;
        case USHIFTR:
            return left >>> right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private long apply(long left, int right) {
        switch (this) {
        case SHIFTL:
            return left << right;
        case SHIFTR:
            return left >> right;
        case USHIFTR:
            return left >>> right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    private long apply(long left, long right) {
        switch (this) {
        case SHIFTL:
            return left << right;
        case SHIFTR:
            return left >> right;
        case USHIFTR:
            return left >>> right;
        default:
            throw new RuntimeException("Unrecognized operator " + this.asStr());
        }
    }

    public Expr<IntSort> buildZ3Expr(
            Expr<IntSort> left,
            boolean isLeftLong,
            Expr<IntSort> right,
            boolean isRightLong,
            Context ctx) {
        BitVecExpr leftInBV = ctx.mkInt2BV(isLeftLong ? 64 : 32, left);
        BitVecExpr rightInBV = ctx.mkInt2BV(isRightLong ? 64 : 32, right);
        BitVecExpr res;
        switch (this) {
        case SHIFTL:
            res = ctx.mkBVSHL(leftInBV, rightInBV);
            break;
        case SHIFTR:
            res = ctx.mkBVASHR(leftInBV, rightInBV);
            break;
        case USHIFTR:
            res = ctx.mkBVLSHR(leftInBV, rightInBV);
            break;
        default:
            throw new RuntimeException("Unsupported operator " + this.asStr());
        }
        return ctx.mkBV2Int(res, true);
    }
}
