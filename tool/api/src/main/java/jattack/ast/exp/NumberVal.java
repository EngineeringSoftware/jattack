package jattack.ast.exp;

import jattack.ast.nodetypes.NodeWithType;
import jattack.ast.exp.iterator.LitItr;
import jattack.driver.Driver;
import jattack.util.TypeUtil;
import jattack.util.UniqueList;

import java.util.List;

/**
 * Number literal expression, i.e, short, byte, int, long, float, double
 */
public abstract class NumberVal<T extends Number> extends LitExp<T>
        implements NodeWithType<T> {

    private final UniqueList<T> vals;

    /* Bounds as input. */
    private final boolean isBounded;
    protected Number low; // inclusive
    protected Number high; // exclusive

    /**
     * Constructor only for random exploration.
     */
    public NumberVal() {
        this.vals = null;
        this.isBounded = false;
    }

    /**
     * Constructor only for systematic exploration.
     */
    public NumberVal(List<T> vals) {
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
    public NumberVal(Number low, Number high) {
        if (TypeUtil.compare(low, high) >= 0) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        this.vals = null;
        this.isBounded = true;
        this.low = low;
        this.high = high;
    }

    protected abstract void setBoundedItr();

    @Override
    protected void setItr() {
        if (isBounded) {
            setBoundedItr();
            return;
        }
        itr = new LitItr<>(vals) {
            @Override
            public void next() {
                super.next();
                val = iterator.next();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void stepRand() {
        val = (T) (isBounded ?
                Driver.rand.nextNumber(getType(), low, high) :
                Driver.rand.nextNumber(getType()));
    }
}
