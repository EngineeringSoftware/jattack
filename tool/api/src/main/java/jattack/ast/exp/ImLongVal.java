package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable long literal expression.
 */
public final class ImLongVal extends ImVal<Long> {

    public ImLongVal(long val) {
        super(val);
    }

    @Override
    public String asStr() {
        return getVal() + "L";
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}

