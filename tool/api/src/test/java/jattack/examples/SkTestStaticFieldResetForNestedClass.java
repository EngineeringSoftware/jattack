package jattack.examples;

import static jattack.Boom.*;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

/**
 * Test static fields of nested classes can be re-intialized correctly
 * before starting generating a new program from the template.
 */
public class SkTestStaticFieldResetForNestedClass {

    private static class C {
        private final static int primFinal = 1;
        private static int primWithInit = 1;
        private static long primWOInit;

        private final static String strFinal = "foo";
        private static String strWithInit = "foo";
        private static String strWOInit;

        private final static int[] finalArr = {0, 0, 0};
        private static int[] arrWithInit = {0, 0, 0};
        private static int[] arrWOInitializer;
    }

    /**
     * Same logic as {@link SkTestStaticFieldReset#m()}.
     */
    @Entry
    public static void m() {
        // update static fields
        if (C.primWithInit == 1) {
            C.primWithInit = intVal(5, 10).eval();
        }
        if (C.primWOInit == 0) {
            C.primWOInit = longVal(10L, 20L).eval();
        }
        String hello = "hello";
        if (C.strWithInit.equals("foo")) {
            C.strWithInit = refId(String.class, "hello").eval();
        }
        if (C.strWOInit == null) {
            C.strWOInit = refId(String.class, "hello").eval();
        }
        if (C.arrWithInit[1] == 0) {
            C.arrWithInit[1] = intVal(5, 10).eval();
        }
        if (C.arrWOInitializer == null) {
            int[] a = {5, 6, 7};
            C.arrWOInitializer = intArrId("a").eval();
        }
        if (C.finalArr[1] == 0) {
            C.finalArr[1] = intVal(5, 10).eval();
        }

        // check final
        if (C.primFinal != 1) {
            throw new InvokedFromNotDriverException();
        }
        if (!C.strFinal.equals("foo")) {
            throw new InvokedFromNotDriverException();
        }
        int x = intVal().eval();
    }
}
