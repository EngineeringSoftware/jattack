package jattack.ast.exp;

import jattack.ast.visitor.Visitor;
import jattack.util.TypeUtil;

public final class ImCharVal extends ImVal<Character> {

    public ImCharVal(char val) {
        super(val);
    }

    @Override
    public String asStr() {
        // Needs to escape single quote and backslash
        return "'" + TypeUtil.charAsString(getVal()) + "'";
    }

    @Override
    public Class<Character> getType() {
        return Character.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
