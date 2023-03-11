package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestHoleInStaticInitializer {

    static int f = makeInt();

    static {
        f = intVal().eval();
    }

    @Entry
    public static int m() {
        return makeInt();
    }

    static int makeInt() {
        return intVal().eval();
    }
}
