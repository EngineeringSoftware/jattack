package jattack.ast.exp;

import jattack.ast.exp.iterator.RangeItr;
import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Short literal expression.
 */
public class ShortVal extends NumberVal<Short> {

    /**
     * Constructor only for random exploration.
     */
    public ShortVal() {
        super();
    }

    /**
     * Constructor only for systematic exploration.
     */
    public ShortVal(List<Short> vals) {
        super(vals);
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public ShortVal(short low, short high) {
        super(low, high);
    }

    @Override
    public Class<Short> getType() {
        return Short.class;
    }

    @Override
    protected void setBoundedItr() {
        itr = new RangeItr((short) low, (short) high) {
            @Override
            public void next() {
                super.next();
                val = (short) getCurrent();
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
