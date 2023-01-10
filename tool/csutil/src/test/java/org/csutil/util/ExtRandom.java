package org.csutil.util;

import java.util.Random;

/**
 * Extended Random class that generates random byte/char/short.
 */
public class ExtRandom extends Random {

    public byte nextByte() {
        return (byte) next(8);
    }

    public char nextChar() {
        return (char) next(16); // non-negative
    }

    public short nextShort() {
        return (short) next(16);
    }
}
