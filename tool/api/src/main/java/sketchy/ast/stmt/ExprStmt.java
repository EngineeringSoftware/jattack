package sketchy.ast.stmt;

import sketchy.ast.exp.Exp;
import sketchy.ast.visitor.Visitor;

/**
 * Expression as a statement.
 */
public class ExprStmt extends Stmt {

    private final Exp<?> exp;

    public ExprStmt(Exp<?> exp) {
        this.exp = exp;
    }

    @Override
    protected void setItr() {
        itr = exp.itr();
    }

    @Override
    public void stepRand() {
        exp.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return exp.hasRandChoice();
    }

    @Override
    public void accept(Visitor v) {
        v.visitStmt(this);
    }

    public Exp<?> getExpression() {
        return exp;
    }
}
