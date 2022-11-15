package jattack.ast.exp;

import jattack.ast.exp.iterator.LitItr;
import jattack.ast.exp.iterator.RangeItr;
import jattack.ast.nodetypes.TerminalNode;
import jattack.ast.visitor.Visitor;
import jattack.driver.Driver;
import jattack.util.UniqueList;

import java.util.List;

/**
 * Long literal expression.
 */
public class LongVal extends Exp<Long> implements TerminalNode<Long> {

    private final UniqueList<Long> vals;

    private Long val;

    /* Bounds as input. */
    private final boolean isBounded;
    private long low; // inclusive
    private long high; // exclusive

    /**
     * Constructor only for random exploration
     */
    public LongVal() {
        this.vals = null;
        isBounded = false;
    }

    /**
     * Constructor only for systematic exploration.
     */
    public LongVal(List<Long> vals) {
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
    public LongVal(long low, long high) {
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
        return val.toString() + "L";
    }

    @Override
    public Long getVal() {
        return val;
    }

    @Override
    protected void setItr() {
        itr = isBounded ?
                new RangeItr(low, high) {
                    @Override
                    public void next() {
                        super.next();
                        val = getCurrent(); } } :
                new LitItr<>(vals) {
                    @Override
                    public void next() {
                        super.next();
                        val = iterator.next(); } };
    }

    @Override
    public void stepRand() {
        val = isBounded ?
                Driver.rand.nextLong(low, high) :
                Driver.rand.nextLong();
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
