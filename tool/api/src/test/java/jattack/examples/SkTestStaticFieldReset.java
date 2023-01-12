package jattack.examples;

import static jattack.Boom.*;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

/**
 * Test static fields can be re-initialized correctly before starting
 * generating a new program from the template.
 */
public class SkTestStaticFieldReset {

    private final static int primFinal = 1;
    private static int primWithInit = 1;
    private static long primWOInit;

    private final static String strFinal = "foo";
    private static String strWithInit = "foo";
    private static String strWOInit;

    private final static int[] finalArr = {0, 0, 0};
    private static int[] arrWithInit = {0, 0, 0};
    private static int[] arrWOInitializer;

    /**
     * If the state of the template was not well reset after the
     * generation of the first program completes, the next generated
     * programs will have all holes unfilled, which causes
     * InvokedFromDriver exception since these holes actually should
     * be filled.
     * We check primitives, strings, references as well as final, with
     * initializer and without initializer.
     */
    @Entry
    public static void m() {
        // update static fields
        if (primWithInit == 1) {
            primWithInit = intVal(5, 10).eval();
        }
        if (primWOInit == 0) {
            primWOInit = longVal(10L, 20L).eval();
        }
        String hello = "hello";
        if (strWithInit.equals("foo")) {
            strWithInit = refId(String.class, "hello").eval();
        }
        if (strWOInit == null) {
            strWOInit = refId(String.class, "hello").eval();
        }
        if (arrWithInit[1] == 0) {
            arrWithInit[1] = intVal(5, 10).eval();
        }
        if (arrWOInitializer == null) {
            int[] a = {5, 6, 7};
            arrWOInitializer = intArrId("a").eval();
        }
        if (finalArr[1] == 0) {
            finalArr[1] = intVal(5, 10).eval();
        }

        // check final
        if (primFinal != 1) {
            throw new InvokedFromNotDriverException();
        }
        if (!strFinal.equals("foo")) {
            throw new InvokedFromNotDriverException();
        }
        int x = intVal().eval();
    }
}
