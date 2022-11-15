package jattack.ast.stmt;

import jattack.ast.exp.Exp;
import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.visitor.Visitor;

/**
 * If Statement, with a condition expression, a then statement and an
 * optional else statement.
 */
public class IfStmt extends Stmt {

    private final Exp<Boolean> condition;

    private final Stmt thenStmt;

    private final Stmt elseStmt;

    public IfStmt(Exp<Boolean> condition, Stmt thenStmt) {
        this(condition, thenStmt, null);
    }

    public IfStmt(Exp<Boolean> condition, Stmt thenStmt, Stmt elseStmt) {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    protected void setItr() {
        itr = hasElseBranch() ?
                new ChainItr(condition.itr(), thenStmt.itr(), elseStmt.itr()) :
                new ChainItr(condition.itr(), thenStmt.itr());
    }

    @Override
    public void stepRand() {
        condition.stepRand();
        thenStmt.stepRand();
        if (hasElseBranch()) {
            elseStmt.stepRand();
        }
    }

    @Override
    public boolean hasRandChoice() {
        return condition.hasRandChoice()
                && thenStmt.hasRandChoice()
                && (!hasElseBranch() || elseStmt.hasRandChoice());
    }

    @Override
    public void accept(Visitor v) {
        v.visitStmt(this);
    }

    public Exp<Boolean> getCondition() {
        return condition;
    }

    public Stmt getThenStmt() {
        return thenStmt;
    }

    /**
     * Returns else statement if non-nul, otherwise null.
     */
    public Stmt getElseStmt() {
        return elseStmt;
    }

    public boolean hasElseBranch() {
        return elseStmt != null;
    }
}
