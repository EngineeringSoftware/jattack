package jattack.examples;

import jattack.annotation.*;
import static jattack.Boom.*;

public class SkTestPrimitiveIdApis {

    static boolean f_b;
    static char f_c;
    static byte f_by;
    static short f_s;
    static int f_i;
    static long f_l;
    static float f_f;
    static double f_d;

    @Entry
    public static void m(boolean a_b, char a_c, byte a_by, short a_s,
                         int a_i, long a_l, float a_f, double a_d) {
        boolean b = boolId().eval();
        char c = charId().eval();
        byte by = byteId().eval();
        short s = shortId().eval();
        int i = intId().eval();
        long l = longId().eval();
        float f = floatId().eval();
        double d = doubleId().eval();
    }

    @Arguments
    static Object[] args() {
        return new Object[]{ f_b, f_c, f_by, f_s, f_i, f_l, f_f, f_d };
    }
}
