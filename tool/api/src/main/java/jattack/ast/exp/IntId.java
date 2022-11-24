package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Int identifier expression.
 */
public class IntId extends IdExp<Integer> {

    public IntId(List<String> ids) {
        this(false, ids);
    }

    public IntId(boolean exclude, List<String> ids) {
        super(exclude, ids);
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
