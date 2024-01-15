package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

public class CastExp<T> extends Exp<T> {

    private final Exp<?> exp;
    private final Class<T> type;

    public CastExp(Class<T> type, Exp<?> exp) {
        if (type.isPrimitive()) {
            throw new RuntimeException("target type cannot be primitive type: " + type);
        }
        this.type = type;
        this.exp = exp;
    }

    /**
     * Set the iterator.
     */
    @Override
    protected void setItr() {
        itr = exp.itr();
    }

    /**
     * Step to next random choice;
     */
    @Override
    public void stepRand() {
        exp.stepRand();
    }

    /**
     * Returns if the exp has choice under random search strategy.
     */
    @Override
    public boolean hasRandChoice() {
        return exp.hasRandChoice();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            exp.accept(v);
            v.endVisit(this);
        }
    }
}
