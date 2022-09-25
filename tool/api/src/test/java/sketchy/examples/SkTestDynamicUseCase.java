package sketchy.examples;

import sketchy.annotation.Argument;
import sketchy.annotation.Entry;
import static sketchy.Sketchy.*;

public class SkTestDynamicUseCase {

    static int decide(int x) {
        if (x < 0) {
            return 100;
        } else {
            return -100;
        }
    }

    @Entry
    static int m(int x) {
        // intVal() will work only after we implement hasChoice for
        // every Exp.
        return alt(decide(x), intVal()).eval();
    }

    @Argument(0)
    static int arg0() {
        return -1;
    }
}
