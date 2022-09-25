package sketchy.examples;

import static sketchy.Sketchy.*;
import sketchy.annotation.Entry;

public class SkTestNonCompilableGeneratedProgram {

    static final int A = 0;
    static final int B = 0;
    static int s = 0;

    /* Only one generated program is possible, which is
     * A < y. Neither A < A nor B < B is able to compile so both
     * should be dropped during generation. */
    @Entry
    static void m() {
        while (relation(intId("A"), intId(), LT).eval()) {
            s = 1;
        }
    }
}
