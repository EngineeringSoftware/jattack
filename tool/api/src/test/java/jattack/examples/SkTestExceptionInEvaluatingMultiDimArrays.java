package jattack.examples;

import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestExceptionInEvaluatingMultiDimArrays {

    @Entry
    static void m() {
        int y = intVal(-3, 0).eval();

        try {
            int[][] a1 = null;
            // a1[1] throws NullPointerException but the hole should
            // be filled
            int x1 = intArrAccessExp(refArrAccessExp(int[].class, int[][].class, refId(int[][].class, "a1"), asInt(1))).eval();
            // but this hole should not be filled.
            y = intVal(1, 4).eval();
        } catch (NullPointerException ignored) {}

        try {
            int[][] a2 = {{}};
            // a2[1] throws ArrayIndexOutOfBoundException but the hole
            // should be filled
            int x2 = intArrAccessExp(refArrAccessExp(int[].class, int[][].class, refId(int[][].class, "a2"), asInt(1))).eval();
            // but this hole should not be filled.
            y = intVal(1, 4).eval();
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        try {
            int[][] a3 = {{1}};
            // a3[1] throws ArithmeticException but the hole should be
            // filled
            int x3 = intArrAccessExp(refArrAccessExp(int[].class, int[][].class, refId(int[][].class, "a3"), arithmetic(intId("y"), asInt(0), DIV))).eval();
            // but this hole should not be filled.
            y = intVal(1, 4).eval();
        } catch (ArithmeticException ignored) {}

        if (y > 0) {
            throw new InvokedFromNotDriverException();
        }
    }
}
