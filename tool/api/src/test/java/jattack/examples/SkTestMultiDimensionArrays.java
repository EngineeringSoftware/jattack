package jattack.examples;

import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestMultiDimensionArrays {

    @Entry
    static void m() {
        int[][] a = {{1, 2}, {3, 4}};
        int x = 10 + intArrAccessExp(refArrAccessExp(int[].class, int[][].class)).eval();
        assignStmt(intArrAccessExp(refArrAccessExp(int[].class, int[][].class)), intId()).eval();
        for (int[] a1: a) {
            for (int e : a1) {
                if (e > 10) {
                    // one element should be greater than 10
                    return;
                }
            }
        }
        throw new InvokedFromNotDriverException();
    }
}
