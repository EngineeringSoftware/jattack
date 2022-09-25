package sketchy.ast.exp;

import sketchy.ast.nodetypes.TerminalNode;
import sketchy.ast.exp.iterator.ImItr;
import sketchy.ast.visitor.Visitor;

/**
 * Immutable double literal expression.
 */
public final class ImDoubleVal extends Exp<Double>
        implements TerminalNode<Double> {

    private final double val;

    public ImDoubleVal(double val) {
        this.val = val;
    }

    @Override
    public Double getVal() {
        return val;
    }

    @Override
    public String asStr() {
        return String.valueOf(val);
    }

    @Override
    protected void setItr() {
        itr = new ImItr();
    }

    @Override
    public void stepRand() {}

    @Override
    public boolean hasRandChoice() {
        return true;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
