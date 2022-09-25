package sketchy.util;

import java.util.Random;

public class Rand extends Random {

    public Rand() {
        super();
    }

    public Rand(long seed) {
        super(seed);
    }

    /**
     * Returns a random int value between {@code low} (inclusive) and
     * {@code high} (exclusive).
     */
    public int nextInt(int low, int high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        long range = (long) high - low;
        if (range <= Integer.MAX_VALUE) {
            return low + nextInt((int) range);
        } else {
            // TODO: handle when range is greater than Integer.MAX_VALUE
            throw new IllegalArgumentException("range must not be greater than Integer.MAX_VALUE!");
        }
    }

    public int nextNonNegativeInt() {
        return nextInt() & Integer.MAX_VALUE; // zero out the sign bit
    }

    /**
     * Returns a random long value between {@code low} (inclusive) and
     * {@code high} (exclusive).
     */
    public long nextLong(long low, long high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        long range = high - low;
        if (range < 0) {
            // TODO: handle when range is greater than Long.MAX_VALUE
            throw new IllegalArgumentException("range must not be greater than Long.MAX_VALUE!");
        }
        if (range < Integer.MAX_VALUE) {
            return low + nextInt((int) range);
        } else {
            return low + nextLong(range);
        }
    }

    /**
     * Returns a random long value between {@code 0} (inclusive) and
     * {@code n} (exclusive).
     * <p>
     * Adapted from
     * https://stackoverflow.com/questions/2546078/java-random-long-number-in-0-x-n-range.
     */
    public long nextLong(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive!");
        }
        long bits, val;
        do {
            bits = (nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }

    /**
     * Returns a random double value between {@code low} (inclusive)
     * and {@code high} (exclusive).
     */
    public double nextDouble(double low, double high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        double range = high - low;
        if (!Double.valueOf(range).isInfinite()) {
            return low + range * super.nextDouble();
        } else {
            // TODO: handle when range is greater than Double.MAX_VALUE
            throw new IllegalArgumentException("range must not be greater than Double.MAX_VALUE!");
        }
    }

    /**
     * Returns a random double value between {@code -Double.MAX_VALUE}
     * (inclusive) and {@code Double.MAX_VALUE} (exclusive).
     */
    @Override
    public double nextDouble() {
        double val = super.nextDouble() * Double.MAX_VALUE;
        return nextBoolean() ? val : -val;
    }
}
