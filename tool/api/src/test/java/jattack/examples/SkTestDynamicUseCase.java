package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;
import static jattack.Boom.*;

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
        return alt(decide(x), intVal()).eval();
    }

    @Argument(1)
    static int arg1() {
        return -1;
    }
}
