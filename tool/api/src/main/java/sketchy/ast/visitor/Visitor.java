package sketchy.ast.visitor;

import sketchy.ast.exp.AssignExp;
import sketchy.ast.exp.LongVal;
import sketchy.ast.exp.PreIncExp;
import sketchy.ast.exp.RefId;
import sketchy.ast.exp.ShiftExp;
import sketchy.ast.exp.DoubleId;
import sketchy.ast.exp.DoubleVal;
import sketchy.ast.exp.AltExp;
import sketchy.ast.exp.BAriExp;
import sketchy.ast.exp.BoolId;
import sketchy.ast.exp.BoolVal;
import sketchy.ast.exp.ImBoolVal;
import sketchy.ast.exp.ImDoubleVal;
import sketchy.ast.exp.ImIntVal;
import sketchy.ast.exp.IntArrVal;
import sketchy.ast.exp.IntId;
import sketchy.ast.exp.IntVal;
import sketchy.ast.exp.LogExp;
import sketchy.ast.exp.RefArrAccessExp;
import sketchy.ast.exp.RelExp;
import sketchy.ast.operator.OpNode;
import sketchy.ast.operator.Op;
import sketchy.ast.stmt.AltStmt;
import sketchy.ast.stmt.BlockStmt;
import sketchy.ast.stmt.ExprStmt;
import sketchy.ast.stmt.IfStmt;
import sketchy.ast.stmt.TryStmt;
import sketchy.ast.stmt.WhileStmt;

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

    public boolean visit(IntId node) { return true; }

    public void endVisit(IntId node) {}

    public boolean visit(IntVal node) { return true; }

    public void endVisit(IntVal node) {}

    public boolean visit(ImIntVal node) { return true;}

    public void endVisit(ImIntVal node) {}

    public boolean visit(DoubleId node) { return true; }

    public void endVisit(DoubleId node) {}

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

    public boolean visit(BoolId node) { return true; }

    public void endVisit(BoolId node) {}

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
