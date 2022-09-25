package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestIntArrRelatedApis {

    @Entry()
    public static void m() {
        testIntArrValOnlyLenBounded();
        testIntArrValFullyBounded();
        testIntArrValWithLen0();
    }

    private static void testIntArrValOnlyLenBounded() {
        int x = intArrAccessExp(intArrVal(1, 3)).eval();
    }

    private static void testIntArrValFullyBounded() {
        int z = intArrAccessExp(intArrVal(1, 3, 0, 1)).eval();
    }

    private static void testIntArrValWithLen0() {
        int y = intArrAccessExp(intArrVal(0, 2, 0, 1)).eval();
    }
}
