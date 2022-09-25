package sketchy.ast.exp;

import sketchy.ast.nodetypes.NodeWithOperator;
import sketchy.ast.exp.iterator.ChainItr;
import sketchy.ast.operator.AriOp;
import sketchy.ast.operator.OpNode;
import sketchy.ast.visitor.Visitor;

import java.util.List;

/**
 * Binary arithmetic expression.
 */
public class BAriExp<T extends Number> extends Exp<T> implements NodeWithOperator<AriOp> {

    // We assume left and right are not the same object.
    private final Exp<T> left;

    private final Exp<T> right;

    private final OpNode<AriOp> op;

    public BAriExp(Exp<T> left, Exp<T> right, List<AriOp> operators) {
        this.left = left;
        this.right = right;
        this.op = new OpNode<>(operators);
    }

    @Override
    public AriOp getOp() {
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
