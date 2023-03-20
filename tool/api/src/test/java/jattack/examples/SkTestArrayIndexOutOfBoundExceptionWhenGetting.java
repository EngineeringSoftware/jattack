package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestArrayIndexOutOfBoundExceptionWhenGetting {

    @Entry
    static void m() {
        int[] arr = { 1, 2 };
        int x = intArrAccessExp("arr", intVal(3, 10)).eval();
        int y = intVal().eval();
    }
}
