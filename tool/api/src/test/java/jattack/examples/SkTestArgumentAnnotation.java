package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestArgumentAnnotation {

    @Entry
    static void m(int x, String s, double... ys) {
        if (x != 1) {
            throw new InvokedFromNotDriverException();
        }
        int y = intId().eval();
        String z = refId(String.class).eval();
        double l = doubleArrAccessExp().eval();
        if (!s.equals("hello")) {
            throw new InvokedFromNotDriverException();
        }
    }

    @Argument(1)
    static int arg1() {
        return 1;
    }

    @Argument(2)
    static String argSecond() {
        return "hello";
    }

    @Argument(3)
    static double[] arg3rd() {
        return new double[]{1.1, 2.2, 3.3};
    }
}