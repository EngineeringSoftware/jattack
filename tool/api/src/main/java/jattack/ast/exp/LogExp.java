package jattack.ast.exp;

import jattack.ast.nodetypes.NodeWithOperator;
import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.operator.LogOp;
import jattack.ast.operator.OpNode;
import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Logical expression.
 */
public class LogExp extends Exp<Boolean> implements NodeWithOperator<LogOp> {

    private final Exp<Boolean> left;

    private final Exp<Boolean> right;

    private final OpNode<LogOp> op;

    public LogExp(Exp<Boolean> left, Exp<Boolean> right, List<LogOp> operators) {
        this.left = left;
        this.right = right;
        this.op = new OpNode<>(operators);
    }

    @Override
    public LogOp getOp() {
        return op.getOp();
    }

    @Override
    protected void setItr() {
        itr = new ChainItr(op.itr(), left.itr(), right.itr());
    }

    @Override
    public void stepRand() {
        left.stepRand();
        right.stepRand();
        op.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return left.hasRandChoice() && right.hasRandChoice() && op.hasRandChoice();
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            left.accept(v);
            op.accept(v); // for short circuit evaluation
            right.accept(v);
            v.endVisit(this);
        }
    }
}
