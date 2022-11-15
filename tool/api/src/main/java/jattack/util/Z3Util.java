package jattack.util;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

/**
 * Utility class for Z3 solver.
 */
public class Z3Util {

    public enum SatSatus {
        VALID,
        UNSATISFIABLE,
        CONTINGENT,
        UNKNOWN
    }

    @SuppressWarnings("unchecked")
    public static SatSatus check(BoolExpr f, Context ctx) {
        Solver s = ctx.mkSolver("QF_BV");
        s.add(f);
        Status res = s.check();
        if (res == Status.UNKNOWN) {
            return SatSatus.UNKNOWN;
        }
        if (res == Status.UNSATISFIABLE) {
            return SatSatus.UNSATISFIABLE;
        }
        // Satisfiable
        s.reset();
        s.add(ctx.mkNot(f));
        res = s.check();
        if (res == Status.UNKNOWN) {
            return SatSatus.UNKNOWN;
        }
        if (res == Status.UNSATISFIABLE) {
            return SatSatus.VALID;
        }
        return SatSatus.CONTINGENT;
    }
}
