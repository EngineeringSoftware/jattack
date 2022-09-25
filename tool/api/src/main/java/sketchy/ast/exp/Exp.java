package sketchy.ast.exp;

import sketchy.ast.Node;
import sketchy.ast.visitor.EvalVisitor;

/**
 * Abstract class for all expressions.
 * @param <T> the type of value evaluated from this expression
 */
public abstract class Exp<T> extends Node<T> {

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
