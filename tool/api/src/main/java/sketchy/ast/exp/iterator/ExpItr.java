package sketchy.ast.exp.iterator;

import sketchy.ast.exp.Exp;

/**
 * Abstract class for all iterators used by {@link Exp}.
 */
public abstract class ExpItr implements Itr {

    private boolean isReset;

    protected ExpItr() {
        isReset = true;
    }

    public void next() {
        if (!hasNext()) {
            throw new RuntimeException("No next!");
        }
        isReset = false;
    }

    public void reset() {
        isReset = true;
    }

    public abstract boolean hasNext();

    public boolean isReset() {
        return isReset;
    }
}
