package jattack.ast.exp.iterator;

/**
 * Range iterator with a given range of integers.
 */
public class RangeItr extends ExpItr {

    private final long low;
    private final long high;

    private long current;

    public RangeItr(long low, long high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        if (low == Long.MIN_VALUE) {
            // TODO: support it
            throw new IllegalArgumentException("low must be strictly greater than Long.MIN_VALUE!");
        }
        this.low = low;
        this.high = high;
        reset();
    }

    @Override
    public void next() {
        super.next();
        current++;
    }

    @Override
    public boolean hasNext() {
        return current < high - 1;
    }

    @Override
    public void reset() {
        super.reset();
        current = low - 1L; // long
    }

    public long getCurrent() {
        if (isReset()) {
            throw new RuntimeException("current is unavailable when reset.");
        }
        return current;
    }
}
