package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable short literal expression.
 */
public final class ImShortVal extends ImVal<Short> {

    public ImShortVal(short val) {
        super(val);
    }

    @Override
    public Class<Short> getType() {
        return Short.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
