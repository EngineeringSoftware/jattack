package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable int literal expression.
 */
public final class ImIntVal extends ImVal<Integer> {

    public ImIntVal(int val) {
        super(val);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}

