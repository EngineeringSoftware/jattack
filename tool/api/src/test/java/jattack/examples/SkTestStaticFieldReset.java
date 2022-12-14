package jattack.examples;

import static jattack.Boom.*;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

/**
 * Test static fields can be re-initialized correctly before starting
 * generating a new program from the template.
 */
public class SkTestStaticFieldReset {

    private final static int finalArr[] = {0, 0, 0};
    private static int arr[] = {0, 0, 0};
    private final static int finalI = 1;
    private final static String finalS = "foo";
    private static int i = 1;
    private static long l;
    private static String s;

    /**
     * During generating the first program, arr[1] will be set to a
     * non-zero number; if the field arr is not correctly reset, then
     * the following generated programs will not have the first hole
     * filled. However, when executing those generated programs, the
     * first hole will be reached and executed and we will see an
     * InvokedFromDriver exception.
     * For final fields, we directly check if the value is changed; if
     * so we explicitly throw exceptions.
     * Similarly, we check if the reset mechanism work well for final
     * fields and primitive fields, with initialized values and
     * without initialized values.
     */
    @Entry
    public static void m() {
        if (arr[1] == 0) {
            arr[1] = intVal(5, 10).eval();
        }
        if (finalArr[1] == 0) {
            finalArr[1] = intVal(5, 10).eval();
        }
        if (i == 1) {
            i = intVal(5, 10).eval();
        }
        if (l == 0) {
            l = longVal(10L, 20L).eval();
        }
        String hello = "hello";
        if (s == null) {
            s = refId(String.class, "hello").eval();
        }
        if (finalI != 1) {
            throw new InvokedFromNotDriverException();
        }
        if (!finalS.equals("foo")) {
            throw new InvokedFromNotDriverException();
        }
        int x = intVal().eval();
    }
}
