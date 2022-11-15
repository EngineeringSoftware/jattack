package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Bool identifier expression.
 */
public class BoolId extends IdExp<Boolean> {

    public BoolId(List<String> ids) {
        this(false, ids);
    }

    public BoolId(boolean exclude, List<String> ids) {
        super(exclude, ids);
    }

    @Override
    public Class<?> getIdType() {
        return Boolean.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
