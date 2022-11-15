package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestDoubleIdApis {

    private static double s1 = 0.0;

    @Entry
    public static double m() {
        double x = 1.0;
        double y = 5.0;
        if (relation(doubleId("x"), doubleId("x"), LT).eval()) {
            s1 += doubleId().eval();
        } else {
            s1 += doubleId().eval();
        }
        return s1 + x + y;
    }
}
