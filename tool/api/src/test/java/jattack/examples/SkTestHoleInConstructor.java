package jattack.examples;

import static jattack.Boom.*;

import jattack.annotation.Entry;

public class SkTestHoleInConstructor {

    @Entry
    static void m() {
        C c = new C();
    }

    static class C extends D {
        C(int i) {
            super(intId().eval());
        }

        C() {
            this(intVal().eval());
        }
    }

    static class D {
        int i;

        D(int i) {
            this.i = intId().eval();
        }
    }
}
