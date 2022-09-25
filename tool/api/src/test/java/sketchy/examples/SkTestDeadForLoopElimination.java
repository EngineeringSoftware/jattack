package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestDeadForLoopElimination {

    static int s = 0;

    @Entry
    public static int m() {
        int i = 0;
        /* If we print intermediate hot filling source this loop is
        supposed to be eliminated. */
        for (; relation(intId("s"), intId("s"), LT).eval() ;) {
            s += intVal().eval();
        }
        s += intVal().eval();
        return s;
    }
}
