package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

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
