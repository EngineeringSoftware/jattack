package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

/**
 * NOTE: Arrays.
 */
public class M4 {

    static int s1;
    static int s2;

    @Entry
    public static int m() {
        int[] arr1 = { s1, s2, intVal().eval(), intVal().eval(), intVal().eval() };
        for (int i = 0; i < arr1.length; ++i) {
            if (logic(relation(intIdOrIntArrAccessExp(true, "i"), intIdOrIntArrAccessExp(true, "i"), LE),
                    relation(intIdOrIntArrAccessExp(true, "i"), intIdOrIntArrAccessExp(true, "i"), LE),
                    OR, AND).eval()) {
                arr1[i] &= arithmetic(intIdOrIntArrAccessExp(true, "i"), intIdOrIntArrAccessExp(true, "i"), SUB, ADD, MUL).eval();
            }
            s1 ^= arithmetic(intIdOrIntArrAccessExp(true, "i"), intIdOrIntArrAccessExp(true, "i"), SUB, ADD, MUL).eval();
        }
        s2 |= arithmetic(intIdOrIntArrAccessExp(), intIdOrIntArrAccessExp(), SUB, ADD, MUL).eval();

        int ret = s1 + s2;
        for (int e : arr1) {
            ret += e;
        }
        return ret;
    }
}
