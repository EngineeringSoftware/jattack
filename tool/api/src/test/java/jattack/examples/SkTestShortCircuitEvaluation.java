package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestShortCircuitEvaluation {

    @Entry
    public static void m() {
        int y = 0;
        if (logic(relation(10, 11, GE),
                  /* ++y > 0 should not be evaluated due to short circuit. */
                  relation(preIncIntExp("y"), 0, GT),
                  AND).eval()) {}
        if (y == 0) {
            // if y gets increased, which means short circuit failed,
            // this hole will not be filled, and we will see a
            // crash when running the generated program with the hole
            // unfilled.
            int z = intVal().eval();
        }
    }
}
