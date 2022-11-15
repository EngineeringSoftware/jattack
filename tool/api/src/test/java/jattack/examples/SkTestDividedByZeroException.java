package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestDividedByZeroException {

    @Entry
    public static int m() {
        int x = 0;
        int z = arithmetic(intVal(), intId(), DIV, MOD).eval();
        return z;
    }
}
