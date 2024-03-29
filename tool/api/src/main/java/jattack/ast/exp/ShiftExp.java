package jattack.ast.exp;

import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.nodetypes.NodeWithOperator;
import jattack.ast.operator.OpNode;
import jattack.ast.operator.ShiftOp;
import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Binary shift expression. {@link N} can only be {@link Integer} or
 * {@link Long}; so is {@link M}.
 */
public class ShiftExp<N extends Number, M extends Number> extends Exp<N>
        implements NodeWithOperator<ShiftOp> {

    private final Exp<N> left;

    private final Exp<M> right;

    private final OpNode<ShiftOp> op;

    public ShiftExp(Exp<N> left, Exp<M> right, List<ShiftOp> operators) {
        this.left = left;
        this.right = right;
        this.op = new OpNode<>(operators);
    }

    @Override
    public ShiftOp getOp() {
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
    public Class<N> getType() {
        return left.getType();
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            left.accept(v);
            right.accept(v);
            v.endVisit(this);
        }
    }

    public Exp<N> getLeft() {
        return left;
    }

    public Exp<M> getRight() {
        return right;
    }
}
