package jattack.examples;

import static jattack.Boom.*;
import jattack.annotation.Entry;

public class SkTestSolverAidWithArrays {

    static int s = 0;

    @Entry
    static void m() {
        int[] arr = { 1 };
        /* If we print intermediate hot filling source code then this
           branch is supposed to be eliminated as it is unsatisfiable.
        */
        if (relation(asIntArrAccess("arr[0]"), asIntArrAccess("arr[0]"), LT).eval()) {
            s += intVal().eval();
        } else {
            s += intVal().eval();
        }
    }
}
