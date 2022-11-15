package jattack.ast.exp;

import jattack.ast.nodetypes.NodeWithSideEffect;
import jattack.ast.visitor.Visitor;

public class PreIncExp<N extends Number> extends Exp<N>
        implements NodeWithSideEffect<N> {

    private final LHSExp<N> id;

    public PreIncExp(LHSExp<N> id) {
        this.id = id;
    }

    @Override
    public void updateVal(N val) {
        id.updateVal(val);
    }

    @Override
    protected void setItr() {
        itr = id.itr();
    }

    @Override
    public void stepRand() {
        id.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return id.hasRandChoice();
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            id.accept(v);
            v.endVisit(this);
        }
    }
}
