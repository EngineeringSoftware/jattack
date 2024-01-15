package jattack.ast.exp;

import jattack.ast.Node;
import jattack.ast.nodetypes.NodeWithType;
import jattack.ast.visitor.EvalVisitor;

/**
 * Abstract class for all expressions.
 * @param <T> the type of value evaluated from this expression
 */
public abstract class Exp<T> extends Node<T>
        implements NodeWithType<T> {

    /**
     * Really evaluate.
     */
    @SuppressWarnings("unchecked")
    protected T evaluate() {
        EvalVisitor v = new EvalVisitor();
        accept(v);
        return (T) v.getResult();
    }
}
