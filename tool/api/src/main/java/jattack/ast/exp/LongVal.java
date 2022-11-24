package jattack.ast.exp;

import jattack.ast.exp.iterator.RangeItr;
import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Long literal expression.
 */
public class LongVal extends NumberVal<Long> {

    /**
     * Constructor only for random exploration
     */
    public LongVal() {
        super();
    }

    /**
     * Constructor only for systematic exploration.
     */
    public LongVal(List<Long> vals) {
        super(vals);
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public LongVal(long low, long high) {
        super(low, high);
    }

    @Override
    public String asStr() {
        return val + "L";
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    protected void setBoundedItr() {
        itr = new RangeItr((long) low, (long) high) {
            @Override
            public void next() {
                super.next();
                val = getCurrent();
            }
        };
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
