package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestHoleInStaticInitializer {

    final static int f = makeInt();

    @Entry
    public static int m() {
        return makeInt();
    }

    static int makeInt() {
        return intVal().eval();
    }
}
