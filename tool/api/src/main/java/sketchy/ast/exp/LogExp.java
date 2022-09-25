package sketchy.ast.exp;

import sketchy.ast.nodetypes.NodeWithOperator;
import sketchy.ast.exp.iterator.ChainItr;
import sketchy.ast.operator.LogOp;
import sketchy.ast.operator.OpNode;
import sketchy.ast.visitor.Visitor;

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
    public void accept(Visitor v) {
        if (v.visit(this)) {
            left.accept(v);
            op.accept(v); // for short circuit evaluation
            right.accept(v);
            v.endVisit(this);
        }
    }
}
