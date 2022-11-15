package jattack.ast.exp;

import jattack.ast.nodetypes.NodeWithOperator;
import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.operator.OpNode;
import jattack.ast.operator.RelOp;
import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Relational expression.
 */
public class RelExp<N extends Number> extends Exp<Boolean> implements NodeWithOperator<RelOp> {

    private final Exp<N> left;

    private final Exp<N> right;

    private final OpNode<RelOp> op;

    public RelExp(Exp<N> left, Exp<N> right, List<RelOp> operators) {
        this.left = left;
        this.right = right;
        this.op = new OpNode<>(operators);
    }

    @Override
    public RelOp getOp() {
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
    public void accept(Visitor v) {
        if (v.visit(this)) {
            left.accept(v);
            right.accept(v);
            v.endVisit(this);
        }
    }
}
