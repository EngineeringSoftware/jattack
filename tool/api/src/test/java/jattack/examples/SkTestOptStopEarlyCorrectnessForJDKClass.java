package jattack.examples;

import jattack.annotation.Entry;

import java.util.concurrent.atomic.AtomicReference;

import static jattack.Boom.*;

public class SkTestOptStopEarlyCorrectnessForJDKClass {

    static AtomicReference<C> c = new AtomicReference<>(null);

    static class C {
        int f = 0;
    }

    @Entry
    static void testObject() {
        if (c.get() != null) {
            // This hole is supposed to be filled if the entry method
            // is invoked more than once.
            // However, if stop early optimization does not work
            // correctly, it could be missed and would cause an
            // InvokedFromNotDriverException when executing generated
            // programs.
            int x = intVal().eval();
            return;
        }
        int y = intVal().eval();
        // set c
        c.compareAndSet(null, new C());
    }
}
