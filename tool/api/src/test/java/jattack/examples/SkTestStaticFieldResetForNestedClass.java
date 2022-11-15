package jattack.examples;

import static jattack.Boom.*;
import jattack.annotation.Entry;

/**
 * Test static fields of nested classes can be re-intialized correctly
 * before starting generating a new program from the template.
 */
public class SkTestStaticFieldResetForNestedClass {

    private static class C {
        private final static int finalArr[] = {0, 0, 0};
        private static int arr[] = {0, 0, 0};
        private final static int finalI = 1;
        private final static String finalS = "foo";
        private static int i = 1;
        private static long l;
        private static String s;
    }

    /**
     * During generating the first program, arr[1] will be set to a
     * non-zero number; if the field arr is not correctly reset, then
     * the following generated programs will not have the first hole
     * filled. However, when executing those generated programs, the
     * first hole will be reached and executed and we will see an
     * InvokedFromDriver exception.
     * Similarly, we check if the reset mechanism work well for final
     * fields and primitive fields, with initialized values and
     * without initialized values.
     */
    @Entry
    public static void m() {
        if (C.arr[1] == 0) {
            C.arr[1] = intVal(5, 10).eval();
        }
        if (C.finalArr[1] == 0) {
            C.finalArr[1] = intVal(5, 10).eval();
        }
        if (C.i == 1) {
            C.i = intVal(5, 10).eval();
        }
        if (C.l == 0) {
            C.l = longVal(10L, 20L).eval();
        }
        String hello = "hello";
        if (C.s == null) {
            C.s = refId(String.class, "hello").eval();
        }
        int x = intVal().eval();
    }
}
