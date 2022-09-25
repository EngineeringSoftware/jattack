package sketchy.ast.exp.iterator;

import java.util.Iterator;
import java.util.List;

/**
 * Chain iterator to concatenate more than one iterator.
 */
public class ChainItr extends ExpItr {

    private final Itr itr;

    /**
     * Constructor taking varargs.
     */
    public ChainItr(Itr itr0, Itr itr1, Itr... itrs) {
        CombItr combItr = new CombItr(itr0, itr1);
        for (Itr itr : itrs) {
            combItr = new CombItr(combItr, itr);
        }
        this.itr = combItr;
    }

    /**
     * Constructor taking a list.
     */
    public ChainItr(List<Itr> itrs) {
        if (itrs.isEmpty()) {
            throw new IllegalArgumentException("itrs cannot be empty!");
        }
        if (itrs.size() == 1) {
            this.itr = itrs.get(0);
            return;
        }
        // itrs.size() >= 2
        Iterator<Itr> it = itrs.iterator();
        CombItr combItr = new CombItr(it.next(), it.next());
        while (it.hasNext()) {
            combItr = new CombItr(combItr, it.next());
        }
        this.itr = combItr;
    }

    @Override
    public void next() {
        super.next();
        itr.next();
    }

    @Override
    public void reset() {
        super.reset();
        itr.reset();
    }

    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }
}
