package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable double literal expression.
 */
public final class ImDoubleVal extends ImVal<Double> {

    public ImDoubleVal(double val) {
        super(val);
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
