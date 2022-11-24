package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

/**
 * Immutable boolean literal expression.
 */
public class ImBoolVal extends ImVal<Boolean> {

    public ImBoolVal(boolean val) {
        super(val);
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
