package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable byte literal expression.
 */
public final class ImByteVal extends ImVal<Byte> {

    public ImByteVal(byte val) {
        super(val);
    }

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
