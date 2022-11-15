package jattack.ast.exp.iterator;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator for a literal node such as IntVal, IntId, OpNode.
 * @param <T>
 */
public abstract class LitItr<T> extends ExpItr {

    private final List<T> values;

    protected Iterator<T> iterator;

    protected LitItr(List<T> values) {
        this.values = values;
        this.iterator = this.values.iterator();
    }

    @Override
    public void reset() {
        super.reset();
        iterator = values.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
