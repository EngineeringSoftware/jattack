package jattack.ast.exp;

import jattack.ast.nodetypes.TerminalNode;
import jattack.ast.exp.iterator.LitItr;
import jattack.ast.visitor.Visitor;
import jattack.driver.Driver;
import jattack.util.UniqueList;

/**
 * Boolean literal expression.
 */
public class BoolVal extends Exp<Boolean> implements TerminalNode<Boolean> {

    private final UniqueList<Boolean> vals;

    private boolean val;

    public BoolVal() {
        this.vals = new UniqueList<>();
        vals.add(false);
        vals.add(true);
    }

    @Override
    public Boolean getVal() {
        return val;
    }

    @Override
    public String asStr() {
        return String.valueOf(val);
    }

    @Override
    protected void setItr() {
        itr = new LitItr<>(vals) {
            @Override
            public void next() {
                super.next();
                val = iterator.next();
            }
        };
    }

    @Override
    public void stepRand() {
        val = Driver.rand.nextBoolean();
    }

    @Override
    public boolean hasRandChoice() {
        return true;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
