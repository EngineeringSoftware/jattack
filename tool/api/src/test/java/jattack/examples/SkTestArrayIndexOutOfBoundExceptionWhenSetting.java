package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestArrayIndexOutOfBoundExceptionWhenSetting {

    @Entry
    static void m() {
        int[] arr = { 1, 2 };
        assignStmt(intArrAccessExp("arr", intVal(3, 10)), intVal()).eval();
        int y = intVal().eval();
    }
}
