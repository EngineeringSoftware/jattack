package jattack.ast;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import jattack.ast.exp.Exp;
import jattack.ast.visitor.Z3ExprBuilder;
import jattack.driver.Driver;
import jattack.util.Z3Util;

import static jattack.Boom.*;

/**
 * Test Z3 solver.
 */
public class Z3Test {
    @BeforeClass
    public static void configurate() {
        Driver.isDriven = true;
    }

    @Test
    public void testContingent() {
        Exp exp = relation(intId("x"), intId("y"), GT);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.CONTINGENT, checkSat(exp));
    }

    @Test
    public void testValid() {
        Exp exp = relation(intId("z"), intId("z"), GE);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.VALID, checkSat(exp));
    }

    @Test
    public void testUnsat() {
        Exp exp = relation(intId("x"), intId("x"), NE);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.UNSATISFIABLE, checkSat(exp));
    }

    @Test
    public void testArray() {
        Exp exp = relation(asIntArrAccess("arr[0]"), asIntArrAccess("arr[0]"), LT);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.UNSATISFIABLE, checkSat(exp));
    }

    @Test
    public void testUnsatLogicExpWithBool() {
        Exp exp = logic(boolId("b"), asBool(false), AND);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.UNSATISFIABLE, checkSat(exp));
    }

    @Test
    public void testValidLogicExpWithBool() {
        Exp exp = logic(boolId("b"), asBool(true), OR);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.VALID, checkSat(exp));
    }

    @Test
    public void testContingentLogicExpWithBool() {
        Exp exp = logic(boolId("b"), asBool(true), AND);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.CONTINGENT, checkSat(exp));
    }

    @Test
    public void testExpWithIntVal() {
        Exp exp = relation(asInt(10), asInt(10), GT);
        exp.stepRand();
        Assert.assertEquals(Z3Util.SatSatus.UNSATISFIABLE, checkSat(exp));
    }

    private static Z3Util.SatSatus checkSat(Exp exp) {
        Z3ExprBuilder v = new Z3ExprBuilder();
        exp.accept(v);
        Expr z3expr = v.getZ3Expr();
        return z3expr == null ?
                null :
                Z3Util.check((BoolExpr) z3expr, v.getContext());
    }
}
