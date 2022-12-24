package jattack.ast.exp;

import jattack.ast.nodetypes.TerminalNode;

/**
 * A base class for all literal expressions, including boolean,
 * number and String.
 */
public abstract class LitExp<T> extends Exp<T>
        implements TerminalNode<T> {

    protected T val;

    @Override
    public T getVal() {
        return val;
    }

    @Override
    public String asStr() {
        return String.valueOf(val);
    }

    @Override
    public boolean hasRandChoice() {
        return true;
    }
}
