package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestNegativeArraySizeException {

    static int s1;

    @Entry
    public static int m() {
        int[] arr = { 1, 2 };
        int negativeIndex = intVal(3, 10).eval();
        int z = intArrAccessExp("arr", intId("negativeIndex")).eval();
        return z;
    }
}
