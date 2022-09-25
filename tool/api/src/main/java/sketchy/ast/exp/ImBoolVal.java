package sketchy.ast.exp;

import sketchy.ast.nodetypes.TerminalNode;
import sketchy.ast.exp.iterator.ImItr;
import sketchy.ast.visitor.Visitor;

/**
 * Immutable boolean literal expression.
 */
public class ImBoolVal extends Exp<Boolean>
        implements TerminalNode<Boolean> {

    private final boolean val;

    public ImBoolVal(boolean val) {
        this.val = val;
    }

    @Override
    public Boolean getVal() {
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
