package sketchy.ast.exp;

import sketchy.ast.nodetypes.TerminalNode;
import sketchy.ast.exp.iterator.LitItr;
import sketchy.ast.exp.iterator.RangeItr;
import sketchy.ast.visitor.Visitor;
import sketchy.driver.Driver;
import sketchy.util.UniqueList;

import java.util.List;

/**
 * Int literal expression.
 */
public class IntVal extends Exp<Integer> implements TerminalNode<Integer> {

    private final UniqueList<Integer> vals;

    private Integer val;

    /* Bounds as input. */
    private final boolean isBounded;
    private int low; // inclusive
    private int high; // exclusive

    /**
     * Constructor only for random exploration.
     */
    public IntVal() {
        this.vals = null;
        this.isBounded = false;
    }

    /**
     * Constructor only for systematic exploration.
     */
    public IntVal(List<Integer> vals) {
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
    public IntVal(int low, int high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        this.vals = null;
        this.isBounded = true;
        this.low = low;
        this.high = high;
    }

    @Override
    public Integer getVal() {
        return val;
    }

    @Override
    public String asStr() {
        return String.valueOf(val);
    }

    @Override
    protected void setItr() {
        itr = isBounded ?
                new RangeItr(low, high) {
                    @Override
                    public void next() {
                        super.next();
                        val = (int) getCurrent(); } } :
                new LitItr<>(vals) {
                    @Override
                    public void next() {
                        super.next();
                        val = iterator.next(); } };
    }

    @Override
    public void stepRand() {
        val = isBounded ?
                Driver.rand.nextInt(low, high) :
                Driver.rand.nextInt();
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
