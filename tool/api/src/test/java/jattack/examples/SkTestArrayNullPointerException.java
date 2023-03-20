package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestArrayNullPointerException {

    @Entry
    static void m() {
        int[] a = null;
        int l = intArrAccessExp(refId(int[].class), intVal()).eval();
    }
}
