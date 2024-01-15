package jattack.examples;

import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestPrimitiveValApis {

    @Entry
    public static void m() {
        boolean b = boolVal().eval();
        char c = charVal().eval();
        mByte();
        mShort();
        mInt();
        mLong();
        mFloat();
        mDouble();
    }

    private static void mByte() {
        byte n = byteVal().eval();
        byte low = -10;
        byte high = 10;
        byte nRanged = byteVal(low, high).eval();
        if (nRanged >= high || nRanged < low) {
            throw new InvokedFromNotDriverException();
        }
    }

    private static void mShort() {
        short n = shortVal().eval();
        short low = Byte.MIN_VALUE - 10;
        short high = Byte.MAX_VALUE + 10;
        short nRanged = shortVal(low, high).eval();
        if (nRanged >= high || nRanged < low) {
            throw new InvokedFromNotDriverException();
        }
    }

    private static void mInt() {
        int n = intVal().eval();
        int low = Short.MIN_VALUE - 10;
        int high = Short.MAX_VALUE + 10;
        int nRanged = intVal(low, high).eval();
        if (nRanged >= high || nRanged < low) {
            throw new InvokedFromNotDriverException();
        }
    }

    private static void mLong() {
        long n = longVal().eval();
        long low = Integer.MIN_VALUE - 10L;
        long high = Integer.MAX_VALUE + 10L;
        long nRanged = longVal(low, high).eval();
        if (nRanged >= high || nRanged < low) {
            throw new InvokedFromNotDriverException();
        }
    }

    private static void mFloat() {
        float n = floatVal().eval();
        float low = -10.0F;
        float high = 10.0F;
        float nRanged = floatVal(low, high).eval();
        if (nRanged >= high || nRanged < low) {
            throw new InvokedFromNotDriverException();
        }
    }

    private static void mDouble() {
        double n = doubleVal().eval();
        double low = -10.0;
        double high = 10.0;
        double nRanged = doubleVal(low, high).eval();
        if (nRanged >= high || nRanged < low) {
            throw new InvokedFromNotDriverException();
        }
    }
}
