package jattack.ast.exp;

import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.nodetypes.NodeWithSideEffect;
import jattack.ast.visitor.Visitor;

public class AssignExp<T> extends Exp<T> implements NodeWithSideEffect<T> {

    private final LHSExp<T> target;

    private final Exp<T> value;

    @Override
    public void updateVal(T val) {
        target.updateVal(val);
    }

    public AssignExp(LHSExp<T> target, Exp<T> value) {
        this.target = target;
        this.value = value;
    }

    @Override
    protected void setItr() {
        itr = new ChainItr(target.itr(), value.itr());
    }

    @Override
    public void stepRand() {
        target.stepRand();
        value.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return target.hasRandChoice() && value.hasRandChoice();
    }

    @Override
    public Class<T> getType() {
        return target.getType();
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            value.accept(v);
            target.accept(v);
            v.endVisit(this);
        }
    }
}
