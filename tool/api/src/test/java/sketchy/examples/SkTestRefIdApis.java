package sketchy.examples;

import sketchy.annotation.Entry;
import static sketchy.Sketchy.*;

public class SkTestRefIdApis {

    private static int s = 0;

    private static class C {
        private int f;

        private C(int f) {
            this.f = f;
        }
    }

    @Entry
    public static void m() {
        testStringClass();
        testGeneralClass();
    }

    private static void testStringClass() {
        String str1 = "Hello";
        String str2 = "Hello";
        if (refId(String.class).eval().equals(refId(String.class).eval())) {
            s += intVal().eval();
        } else {
            s -= intVal().eval();
        }
    }

    private static void testGeneralClass() {
        C c1 = new C(1);
        C c2 = new C(2);
        C c = refId(C.class).eval();
        int x = c.f;
        if (x > 1) {
            s += intVal().eval();
        } else {
            s -= intVal().eval();
        }
    }
}
