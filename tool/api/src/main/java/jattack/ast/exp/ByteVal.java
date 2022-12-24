package jattack.ast.exp;

import jattack.ast.exp.iterator.RangeItr;
import jattack.ast.visitor.Visitor;

import java.util.List;

public class ByteVal extends NumberVal<Byte> {

    /**
     * Constructor only for random exploration.
     */
    public ByteVal() {
        super();
    }

    /**
     * Constructor only for systematic exploration.
     */
    public ByteVal(List<Byte> vals) {
        super(vals);
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public ByteVal(byte low, byte high) {
        super(low, high);
    }

    @Override
    protected void setBoundedItr() {
        itr = new RangeItr((byte) low, (byte) high) {
            @Override
            public void next() {
                super.next();
                val = (byte) getCurrent();
            }
        };
    }

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
