package jattack.examples;

import jattack.annotation.Arguments;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestArgumentsAnnotation {

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

    @Arguments
    static Object[] args() {
        return new Object[] {1, "hello", new double[]{1.1, 2.2, 3.3}};
    }
}
