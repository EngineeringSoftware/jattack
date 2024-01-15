package jattack.ast.exp;

import jattack.ast.exp.iterator.LitItr;
import jattack.ast.visitor.Visitor;
import jattack.driver.Driver;
import jattack.util.UniqueList;

/**
 * Boolean literal expression.
 */
public class BoolVal extends LitExp<Boolean> {

    private final UniqueList<Boolean> vals;

    /**
     * Constructor for both random and systematic exploration.
     */
    public BoolVal() {
        this.vals = new UniqueList<>();
        vals.add(false);
        vals.add(true);
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
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }

}
