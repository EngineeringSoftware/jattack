package jattack.examples;

import static jattack.Boom.*;

import jattack.annotation.Entry;

public class SkTestClassCastException {

    @Entry
    static void m() {
        Object o = intVal().eval();
        // OK
        int i = cast(Integer.class, refId(Object.class, "o")).eval();
        // ClassCastException
        String s = cast(String.class, refId(Object.class, "o")).eval();
    }
}
