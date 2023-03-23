package jattack.examples;

import static jattack.Boom.*;

import jattack.annotation.Entry;

public class SkTestHoleInConstructor {

    @Entry
    static void m() {
        C c1 = new C();
        C c2 = new C(true);
    }

    static class C extends D {
        private static final int I = 10;

        C(boolean b) {
            // test three invokespecials
            // the second one is the one that instantiates C
            this(new D(intId().eval()), // I
                 intId().eval()); // I
            D d = new D(intId().eval()); // I
        }

        private C(D d, int i) {
            // i, I
            super(intId().eval());
        }

        private C(int i) {
            // i, I
            super(intId().eval());
        }

        C() {
            // I
            this(intId().eval());
        }
    }

    static class D {
        private int i;

        D(int i) {
            // i
            this.i = intId().eval();
        }
    }
}
