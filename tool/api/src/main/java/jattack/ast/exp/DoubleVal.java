package jattack.ast.exp;

import jattack.ast.visitor.Visitor;

import java.util.List;

/**
 * Double literal expression.
 */
public class DoubleVal extends NumberVal<Double> {

    /**
     * Constructor only for random exploration
     */
    public DoubleVal() {
        super();
    }

    /**
     * Constructor only for systematic exploration.
     */
    public DoubleVal(List<Double> vals) {
        super(vals);
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public DoubleVal(double low, double high) {
        super(low, high);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    protected void setBoundedItr() {
        // Bounded range systematic exploration does not make sense
        // for a double.
        throw new RuntimeException(
                "Bounded range systematic exploration does not make" +
                        "sense for a range of double.");
    }

    @Override
    public void accept(Visitor v) {
        if (v.visit(this)) {
            v.endVisit(this);
        }
    }
}
