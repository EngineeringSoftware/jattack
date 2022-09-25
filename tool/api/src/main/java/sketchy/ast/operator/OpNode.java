package sketchy.ast.operator;

import sketchy.ast.Node;
import sketchy.ast.exp.iterator.LitItr;
import sketchy.ast.visitor.Visitor;
import sketchy.driver.Driver;
import sketchy.util.UniqueList;

import java.util.List;

/**
 * Operator node.
 */
public class OpNode<T extends Op> extends Node<Void> {

    private final UniqueList<T> ops;

    private T op;

    public OpNode(List<T> ops) {
        if (ops.isEmpty()) {
            throw new RuntimeException("No operators that can be used!");
        }
        this.ops = new UniqueList<>(ops);
    }

    public T getOp() {
        return op;
    }

    @Override
    protected void setItr() {
        itr = new LitItr<T>(ops) {
            @Override
            public void next() {
                super.next();
                op = iterator.next();
            }
        };
    }

    @Override
    public void stepRand() {
        op = ops.pick(Driver.rand);
    }

    @Override
    public boolean hasRandChoice() {
        return !ops.isEmpty();
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }

    @Override
    public Void evaluate() {
        // Never called.
        return null;
    }
}
