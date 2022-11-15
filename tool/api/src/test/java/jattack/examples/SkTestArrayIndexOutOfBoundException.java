package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestArrayIndexOutOfBoundException {

    static int s1;

    @Entry
    public static int m() {
        int[] arr = { 1, 2 };
        int tooLargeIndex = intVal(3, 10).eval();
        int z = intArrAccessExp("arr", intId("tooLargeIndex")).eval();
        return z;
    }
}
