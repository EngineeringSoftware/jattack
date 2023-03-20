package jattack.examples;

import static jattack.Boom.*;

import jattack.annotation.Entry;

public class SkTestHoleInConstructor {

    @Entry
    static void m() {
        C c = new C();
    }

    static class C extends D {
        private static final int I = 10;

        C(int i) {
            super(intId().eval());
        }

        C() {
            this(intId().eval());
        }
    }

    static class D {
        private int i;

        D(int i) {
            this.i = intId().eval();
        }
    }
}
