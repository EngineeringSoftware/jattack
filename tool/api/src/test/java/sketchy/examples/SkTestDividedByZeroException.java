package sketchy.examples;

import sketchy.annotation.Entry;
import static sketchy.Sketchy.*;

public class SkTestDividedByZeroException {

    @Entry
    public static int m() {
        int x = 0;
        int z = arithmetic(intVal(), intId(), DIV, MOD).eval();
        return z;
    }
}
