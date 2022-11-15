package jattack.ast.exp;

import jattack.ast.exp.iterator.LitItr;
import jattack.ast.nodetypes.TerminalNode;
import jattack.ast.visitor.Visitor;
import jattack.driver.Driver;
import jattack.util.UniqueList;

import java.util.List;

/**
 * Double literal expression.
 */
public class DoubleVal extends Exp<Double> implements TerminalNode<Double> {

    private final UniqueList<Double> vals;

    private Double val;

    /* Bounds as input. */
    private final boolean isBounded;
    private double low; // inclusive
    private double high; // exclusive

    /**
     * Constructor only for random exploration
     */
    public DoubleVal() {
        this.vals = null;
        isBounded = false;
    }

    /**
     * Constructor only for systematic exploration.
     */
    public DoubleVal(List<Double> vals) {
        if (vals.isEmpty()) {
            throw new RuntimeException("No values that can be used!");
        }
        this.vals = new UniqueList<>(vals);
        this.isBounded = false;
        // We leave initialization of val done in eval(). val will not
        // be initialized until next() or step() is called.
    }

    /**
     * Constructor accepting lower and upper bounds.
     */
    public DoubleVal(double low, double high) {
        if (low >= high) {
            throw new RuntimeException("low must be less than high!");
        }
        this.vals = null;
        this.isBounded = true;
        this.low = low;
        this.high = high;
    }

    @Override
    public String asStr() {
        return String.valueOf(val);
    }

    @Override
    public Double getVal() {
        return val;
    }

    /*
     * Systematic exploration does not make sense for a double.
     */
    @Override
    protected void setItr() {
        if (isBounded) {
            throw new RuntimeException("Systematic exploration does not make sense for a range of double.");
        }
        itr = new LitItr<>(vals) {
            @Override
            public void next() {
                super.next();
                val = iterator.next(); } };
    }

    @Override
    public void stepRand() {
        val = isBounded ?
                Driver.rand.nextDouble(low, high) :
                Driver.rand.nextDouble();
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
