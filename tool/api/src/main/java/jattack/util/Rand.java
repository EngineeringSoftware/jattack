package jattack.util;

import java.util.Random;

public class Rand extends Random {

    public Rand() {
        super();
    }

    public Rand(long seed) {
        super(seed);
    }

    public <T extends Number> Number nextNumber(Class<?> type, T low, T high) {
        if (Integer.class.equals(type)) {
            return nextInt((int) low, (int) high);
        } else if (Long.class.equals(type)) {
            return nextLong((long) low, (long) high);
        } else if (Float.class.equals(type)) {
            return nextFloat((float) low, (float) high);
        } else if (Double.class.equals(type)) {
            return nextDouble((double) low, (double) high);
        } else if (Byte.class.equals(type)) {
            return nextByte((byte) low, (byte) high);
        } else if (Short.class.equals(type)) {
            return nextShort((short) low, (short) high);
        } else {
            throw new RuntimeException(
                    "Unexpected type for generate random number: " + type);
        }
    }

    public <T extends Number> Number nextNumber(Class<?> type) {
        if (Integer.class.equals(type)) {
            return nextInt();
        } else if (Long.class.equals(type)) {
            return nextLong();
        } else if (Float.class.equals(type)) {
            return nextFloat();
        } else if (Double.class.equals(type)) {
            return nextDouble();
        } else if (Byte.class.equals(type)) {
            return nextByte();
        } else if (Short.class.equals(type)) {
            return nextShort();
        } else {
            throw new RuntimeException(
                    "Unexpected type for generate random number: " + type);
        }
    }

    /**
     * Return a random byte value between {@code low} (inclusive) and
     * {@code high} (exclusive).
     */
    public byte nextByte(byte low, byte high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        return (byte) (low + nextInt(high - low));
    }

    /**
     * Returns a random short value.
     */
    public byte nextByte() {
        return (byte) next(8);
    }

    /**
     * Return a random short value between {@code low} (inclusive) and
     * {@code high} (exclusive).
     */
    public short nextShort(short low, short high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        return (short) (low + nextInt(high - low));
    }

    /**
     * Returns a random short value.
     */
    public short nextShort() {
        return (short) next(16);
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
     * Returns a random float value between {@code -Float.MAX_VALUE}
     * (inclusive) and {@code Float.MAX_VALUE} (exclusive).
     */
    @Override
    public float nextFloat() {
        float val = super.nextFloat() * Float.MAX_VALUE;
        return nextBoolean() ? val : -val;
    }

    /**
     * Returns a random float value between {@code low} (inclusive)
     * and {@code high} (exclusive).
     */
    public float nextFloat(float low, float high) {
        if (low >= high) {
            throw new IllegalArgumentException("low must be less than high!");
        }
        float range = high - low;
        if (!Float.valueOf(range).isInfinite()) {
            return low + range * super.nextFloat();
        } else {
            // TODO: handle when range is greater than Float.MAX_VALUE
            throw new IllegalArgumentException("range must not be greater than Float.MAX_VALUE!");
        }
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

    /**
     * Returns a random char.
     */
    public char nextChar() {
        return (char) nextInt(1<<16);
    }
}
