package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestShortCircuitEvaluation {

    @Entry
    public static void m() {
        boolean y = true;
        /* boolId() is not supposed to be evaluated. */
        if (logic(relation(10, 11, GE), boolId(), AND).eval()) {
            int z = intVal().eval();
        }
    }
}
