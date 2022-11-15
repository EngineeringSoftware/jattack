package jattack.ast.stmt;

import jattack.ast.Node;
import jattack.ast.exp.iterator.ChainItr;
import jattack.ast.visitor.Visitor;

import java.util.List;
import java.util.stream.Collectors;

public class BlockStmt extends Stmt {

    private final List<Stmt> stmts;

    public BlockStmt(List<Stmt> stmts) {
        this.stmts = stmts;
    }

    public List<Stmt> getStmts() {
        return stmts;
    }

    @Override
    protected void setItr() {
        itr = new ChainItr(
                stmts.stream().map(Node::itr).collect(Collectors.toList())
        );
    }

    @Override
    public void stepRand() {
        for (Stmt s : stmts) {
            s.stepRand();
        }
    }

    @Override
    public boolean hasRandChoice() {
        for (Stmt s : stmts) {
            if (!s.hasRandChoice()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void accept(Visitor v) {
        v.visitStmt(this);
    }
}
