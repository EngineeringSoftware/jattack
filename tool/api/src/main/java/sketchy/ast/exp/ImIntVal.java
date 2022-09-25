package sketchy.ast.exp;

import sketchy.ast.nodetypes.TerminalNode;
import sketchy.ast.exp.iterator.ImItr;
import sketchy.ast.visitor.Visitor;

/**
 * Immutable int literal expression.
 */
// make a common super class for IntVal and ImIntVal
public final class ImIntVal extends Exp<Integer>
        implements TerminalNode<Integer> {

    private final int val;

    public ImIntVal(int val) {
        this.val = val;
    }

    @Override
    public Integer getVal() {
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

