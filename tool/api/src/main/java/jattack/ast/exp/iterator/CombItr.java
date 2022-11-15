package jattack.ast.exp.iterator;

/**
 * Combination iterator to combine two iterators.
 */
public class CombItr extends ExpItr {

    private final Itr lItr;
    private final Itr rItr;

    public CombItr(Itr lItr, Itr rItr) {
        this.lItr = lItr;
        this.rItr = rItr;
    }

    @Override
    public void next() {
        super.next();
        // iteration order: right then left
        if (lItr.hasNext() && !rItr.hasNext()) {
            rItr.reset();
            lItr.next();
        }
        rItr.next();
        if (lItr.isReset()) {
            lItr.next();
        }
    }

    @Override
    public void reset() {
        super.reset();
        lItr.reset();
        rItr.reset();
    }

    @Override
    public boolean hasNext() {
        return lItr.hasNext() || rItr.hasNext();
    }
}
