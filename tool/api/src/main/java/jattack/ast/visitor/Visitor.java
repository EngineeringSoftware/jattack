package jattack.ast.visitor;

import jattack.ast.exp.AssignExp;
import jattack.ast.exp.ByteVal;
import jattack.ast.exp.CharVal;
import jattack.ast.exp.FloatVal;
import jattack.ast.exp.LongVal;
import jattack.ast.exp.PreIncExp;
import jattack.ast.exp.RefId;
import jattack.ast.exp.ShiftExp;
import jattack.ast.exp.DoubleVal;
import jattack.ast.exp.AltExp;
import jattack.ast.exp.BAriExp;
import jattack.ast.exp.BoolVal;
import jattack.ast.exp.ImBoolVal;
import jattack.ast.exp.ImDoubleVal;
import jattack.ast.exp.ImIntVal;
import jattack.ast.exp.IntArrVal;
import jattack.ast.exp.IntVal;
import jattack.ast.exp.LogExp;
import jattack.ast.exp.RefArrAccessExp;
import jattack.ast.exp.RelExp;
import jattack.ast.exp.ShortVal;
import jattack.ast.operator.OpNode;
import jattack.ast.operator.Op;
import jattack.ast.stmt.AltStmt;
import jattack.ast.stmt.BlockStmt;
import jattack.ast.stmt.ExprStmt;
import jattack.ast.stmt.IfStmt;
import jattack.ast.stmt.TryStmt;
import jattack.ast.stmt.WhileStmt;

/**
 * Top of the hierarchy for all visitors.
 */
public class Visitor {
    public boolean visit(OpNode<? extends Op> op) { return true; }

    public void endVisit(OpNode<? extends Op> op) {}

    public <N extends Number> boolean visit(BAriExp<N> node) { return true; }

    public <N extends Number> void endVisit(BAriExp<N> node) {}

    public <N extends Number> boolean visit(ShiftExp<N> node) {
        return true;
    }

    public <N extends Number> void endVisit(ShiftExp<N> node) {}

    public boolean visit(BoolVal node) { return true; }

    public void endVisit(BoolVal node) {}

    public boolean visit(ImBoolVal node) { return true; }

    public void endVisit(ImBoolVal node) {}

    public boolean visit(CharVal node) { return true; }

    public void endVisit(CharVal node) {}

    public boolean visit(ByteVal node) { return true; }

    public void endVisit(ByteVal node) {}

    public boolean visit(ShortVal node) { return true; }

    public void endVisit(ShortVal node) {}

    public boolean visit(FloatVal node) { return true; }

    public void endVisit(FloatVal node) {}

    public boolean visit(IntVal node) { return true; }

    public void endVisit(IntVal node) {}

    public boolean visit(ImIntVal node) { return true;}

    public void endVisit(ImIntVal node) {}

    public boolean visit(DoubleVal node) { return true; }

    public void endVisit(DoubleVal node) {}

    public boolean visit(ImDoubleVal node) { return true;}

    public void endVisit(ImDoubleVal node) {}

    public boolean visit(LongVal node) { return true; }

    public void endVisit(LongVal node) {}

    public boolean visit(LogExp node) { return true; }

    public void endVisit(LogExp node) {}

    public <N extends Number> boolean visit(RelExp<N> node) { return true; }

    public <N extends Number> void endVisit(RelExp<N> node) {}

    public boolean visit(IntArrVal node) { return true; }

    public void endVisit(IntArrVal node) {}

    public <T> boolean visit(AltExp<T> node) { return true; }

    public <T> void endVisit(AltExp<T> node) {}

    public boolean visit(RefArrAccessExp<?, ?> node) { return true; }

    public void endVisit(RefArrAccessExp<?, ?> node) {}

    public boolean visit(RefId<?> node) { return true; }

    public void endVisit(RefId<?> node) {}

    public <N extends Number> boolean visit(PreIncExp<N> node) { return true; }

    public <N extends Number> void endVisit(PreIncExp<N> node) {}

    public <T> boolean visit(AssignExp<T> node) { return true; }

    public <T> void endVisit(AssignExp<T> node) {}

    public void visitStmt(ExprStmt node) {}

    public void visitStmt(IfStmt node) {}

    public void visitStmt(WhileStmt node) {}

    public void visitStmt(AltStmt node) {}

    public void visitStmt(BlockStmt node) {}

    public <T extends Throwable> void visitStmt(TryStmt<T> node) {}
}
