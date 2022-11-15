package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestRefArrRelatedApis {

    static int s = 0;

    static class C {
        int i;
        C(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return "C:" + i;
        }
    }

    @Entry
    public static void m() {
        testRefArrAccessExpWithoutGivenIds();
        testRefArrAccessExpWithGivenIds();
        testRefArrAccessExpWithGivenIndicies();
    }

    private static void testRefArrAccessExpWithoutGivenIds() {
        C[] arr1 = { new C(1), new C(2), new C(3) };
        C[] arr2 = { new C(10), new C(20), new C(30) };
        C x = refArrAccessExp(C.class, C[].class).eval();
        s += x.i;
    }

    private static void testRefArrAccessExpWithGivenIds() {
        C[] arr1 = { new C(1), new C(2), new C(3) };
        C[] arr2 = { new C(10), new C(20), new C(30) }; 
        C x = refArrAccessExp(C.class, C[].class, "arr1").eval();
        s += x.i;
    }


    private static void testRefArrAccessExpWithGivenIndicies() {
        C[] arr1 = { new C(1), new C(2), new C(3) };
        C[] arr2 = { new C(10), new C(20), new C(30) };
        C x = refArrAccessExp(C.class, C[].class, "arr2", intVal(1, 3)).eval();
        s += x.i;
    }
}
