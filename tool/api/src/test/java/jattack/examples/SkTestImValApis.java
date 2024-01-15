package jattack.examples;

import jattack.annotation.*;

import static jattack.Boom.*;

public class SkTestImValApis {

    // Only one generated program is possible.
    @Entry
    public static void m() {
        mBool();
        mChar();
        mByte();
        mShort();
        mInt();
        mLong();
        mFloat();
        mDouble();
    }

    private static void mBool() {
        boolean b = asBool(false).eval();
        boolean x = logic(asBool(false), asBool(true), OR).eval();
    }

    private static void mChar() {
        char c = asChar('a').eval();
        int x = arithmetic(
                cast(Integer.class, asChar('a')),
                cast(Integer.class, asChar('b')),
                ADD).eval();
    }

    private static void mByte() {
        byte b = asByte((byte) 10).eval();
        int x = arithmetic(
                cast(Integer.class, asByte((byte) 10)),
                cast(Integer.class, asByte((byte) 11)),
                ADD).eval();
    }

    private static void mShort() {
        short s = asShort((short) 10).eval();
        int x = arithmetic(
            cast(Integer.class, asShort((short) 10)),
            cast(Integer.class, asShort((short) 11)),
            ADD).eval();
    }

    private static void mInt() {
        int i = asInt(10).eval() + asInt(100).eval();
        boolean b = relation(asInt(10), asInt(11), LT).eval();
    }

    private static void mLong() {
        long l = asLong(10).eval() + asLong(100).eval();
        boolean b = relation(asLong(10L), asLong(11L), LT).eval();
    }

    private static void mFloat() {
        float f = asFloat(10.0f).eval() + asFloat(100.0f).eval();
        boolean b = relation(asFloat(10.0f), asFloat(11.0f), LT).eval();
    }

    private static void mDouble() {
        double d = asDouble(10.0).eval() + asDouble(100.0).eval();
        boolean b = relation(asDouble(10.0), asDouble(11.0), LT).eval();
    }
}
