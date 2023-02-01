package jattack.examples;

import jattack.annotation.Arguments;
import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestPrimitiveArrRelatedApis {

    static boolean[] f_b = {false, false, true};
    static char[] f_c = {'a', 'b', 'c'};
    static byte[] f_by = {0, 1, 2};
    static short[] f_s = {0, 1, 2};
    static int[] f_i = {0, 1, 2};
    static long[] f_l = {0L, 1L, 2L};
    static float[] f_f = {0.0F, 1.0F, 2.0F};
    static double[] f_d = {0.0, 1.0, 2.0};

    @Entry
    static void m(boolean[] a_b, char[] a_c, byte[] a_by, short[] a_s,
                  int[] a_i, long[] a_l, float[] a_f, double[] a_d) {
        boolean b = boolArrAccessExp().eval();
        char c = charArrAccessExp().eval();
        byte by = byteArrAccessExp().eval();
        short s = shortArrAccessExp().eval();
        int i = intArrAccessExp().eval();
        long l = longArrAccessExp().eval();
        float f = floatArrAccessExp().eval();
        double d = doubleArrAccessExp().eval();
    }

    @Arguments
    static Object[] args() {
        return new Object[]{ f_b, f_c, f_by, f_s, f_i, f_l, f_f, f_d };
    }
}
