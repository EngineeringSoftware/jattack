package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable float literal expression.
 */
public final class ImFloatVal extends ImVal<Float> {

    public ImFloatVal(float val) {
        super(val);
    }

    @Override
    public String asStr() {
        return getVal() + "F";
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
