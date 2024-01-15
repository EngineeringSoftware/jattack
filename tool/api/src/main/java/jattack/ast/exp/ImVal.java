package jattack.ast.exp;

import jattack.ast.exp.iterator.ImItr;
import jattack.ast.nodetypes.TerminalNode;

/**
 * Common abstract class of immutable literal expressions.
 */
public abstract class ImVal<T> extends Exp<T>
        implements TerminalNode<T> {

    private final T val;

    public ImVal(T val) {
        this.val = val;
    }

    @Override
    public T getVal() {
        return val;
    }

    @Override
    public String asStr() {
        return String.valueOf(val);
    }

    @Override
    protected void setItr() {
        itr = new ImItr();
    }

    @Override
    public void stepRand() {}

    @Override
    public boolean hasRandChoice() {
        return true;
    }
}
