package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

import java.util.List;

public class FloatVal extends NumberVal<Float> {

    /**
     * Constructor only for random exploration.
     */
    public FloatVal() {
        super();
    }

    /**
     * Constructor only for systematic exploration.
     */
    public FloatVal(List<Float> vals) {
        super(vals);
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public FloatVal(float low, float high) {
        super(low, high);
    }

    @Override
    public String asStr() {
        return val + "F";
    }

    @Override
    protected void setBoundedItr() {
        // Bounded range systematic exploration does not make sense
        // for a double.
        throw new RuntimeException(
                "Bounded range systematic exploration does not make" +
                        "sense for a range of float.");
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
