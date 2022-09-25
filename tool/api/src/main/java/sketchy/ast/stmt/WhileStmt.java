package sketchy.ast.stmt;

import sketchy.ast.exp.Exp;
import sketchy.ast.exp.iterator.ChainItr;
import sketchy.ast.visitor.Visitor;

/**
 * While statement, with a condition expression and a body statement.
 */
public class WhileStmt extends Stmt {

    private final Exp<Boolean> condition;

    private final Stmt body;

    public WhileStmt(Exp<Boolean> condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void setItr() {
        itr = new ChainItr(condition.itr(), body.itr());
    }

    @Override
    public void stepRand() {
        condition.stepRand();
        body.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        return condition.hasRandChoice() && body.hasRandChoice();
    }

    @Override
    public void accept(Visitor v) {
        v.visitStmt(this);
    }

    public Exp<Boolean> getCondition() {
        return condition;
    }

    public Stmt getBody() {
        return body;
    }
}
