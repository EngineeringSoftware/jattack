package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestArithmeticWithDouble {

    private static int s1 = 0;

    @Entry
    public static double m() {
        double a = arithmetic(doubleVal(), doubleVal(), ADD).eval();
        return a + 1.0;
    }
}
