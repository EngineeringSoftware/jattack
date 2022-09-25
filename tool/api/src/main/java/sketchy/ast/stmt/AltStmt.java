package sketchy.ast.stmt;

import sketchy.ast.exp.iterator.Itr;
import sketchy.ast.exp.iterator.LitItr;
import sketchy.ast.visitor.Visitor;
import sketchy.driver.Driver;
import sketchy.util.UniqueList;

import java.util.LinkedList;
import java.util.List;

/**
 * Alternative statement.
 */
// TODO: merge with AltExp somehow, which not quite intuitive since
//   because we want to keep inheritance: one is Exp while the other
//   is Stmt.
public class AltStmt extends Stmt {

    private UniqueList<Stmt> stmts;

    private Stmt stmt;

    private boolean emptyPruned;

    public AltStmt(List<Stmt> stmts) {
        this.stmts = new UniqueList<>(stmts);
        this.stmt = null;
        this.emptyPruned = false;
    }

    @Override
    public void stepRand() {
        removeEmptyStmts();
        // TODO: consider if we need uniform distribution of choices
        //  inside each statement rather than of statement.
        stmt = stmts.pick(Driver.rand);
        stmt.stepRand();
    }

    @Override
    public boolean hasRandChoice() {
        for (Stmt s : stmts) {
            if (s.hasRandChoice()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void setItr() {
        removeEmptyStmts();
        itr = new LitItr<>(stmts) {

            private Itr currItr;

            @Override
            public void next() {
                super.next();
                if (currItr == null || !currItr.hasNext()) {
                    stmt = iterator.next();
                    currItr = stmt.itr();
                }
                currItr.next();
            }

            @Override
            public void reset() {
                super.reset();
                currItr = null;
                for (Stmt s : stmts) {
                    s.itr().reset();
                }
            }

            @Override
            public boolean hasNext() {
                if (isReset()) {
                    return iterator.hasNext();
                }
                return iterator.hasNext() || currItr.hasNext();
            }
        };
    }

    @Override
    public void accept(Visitor v) {
        stmt.accept(v);
    }

    private void removeEmptyStmts() {
        if (emptyPruned) {
            return;
        }
        List<Stmt> nonEmptyStmts = new LinkedList<>();
        for (Stmt s : stmts) {
            if (s.hasChoice()) {
                nonEmptyStmts.add(s);
            }
        }
        if (nonEmptyStmts.isEmpty()) {
            throw new RuntimeException("No statement can be used!");
        }
        stmts = new UniqueList<>(nonEmptyStmts);
        emptyPruned = true;
    }
}
