package jattack.ast.exp;

import jattack.ast.exp.iterator.RangeItr;
import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Int literal expression.
 */
public class IntVal extends NumberVal<Integer> {

    /**
     * Constructor only for random exploration.
     */
    public IntVal() {
        super();
    }

    /**
     * Constructor only for systematic exploration.
     */
    public IntVal(List<Integer> vals) {
        super(vals);
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public IntVal(int low, int high) {
        super(low, high);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    protected void setBoundedItr() {
        itr = new RangeItr((int) low, (int) high) {
            @Override
            public void next() {
                super.next();
                val = (int) getCurrent();
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
