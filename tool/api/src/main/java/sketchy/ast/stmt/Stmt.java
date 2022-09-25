package sketchy.ast.stmt;

import sketchy.ast.Node;
import sketchy.ast.visitor.EvalVisitor;

/**
 * Abstract class for all statements.
 */
public abstract class Stmt extends Node<Void> {

    @Override
    public Void evaluate() {
        EvalVisitor v = new EvalVisitor();
        accept(v);
        return null;
    }
}
