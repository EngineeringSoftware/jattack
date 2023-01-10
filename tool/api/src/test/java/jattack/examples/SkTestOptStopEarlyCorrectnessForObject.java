package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestOptStopEarlyCorrectnessForObject {

    static C c = new C();

    static class C {
        int f = 0;
    }

    @Entry
    static void testObject() {
        if (c.f != 0) {
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
        // set o if c.f is 0
        c.f = 2;
    }
}
