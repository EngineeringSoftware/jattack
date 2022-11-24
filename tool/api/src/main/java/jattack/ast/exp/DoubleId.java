package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Double identifier expression.
 */
public class DoubleId extends IdExp<Double> {

    public DoubleId(List<String> ids) {
        this(false, ids);
    }

    public DoubleId(boolean exclude, List<String> ids) {
        super(ids);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
