package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestRefIdCastToSuperTypeWhenFilled {

    @Entry
    static void m() {
        String s = "hello";
        overridedM(refId(Object.class).eval());
    }

    // If we go to this method, then we will get an
    // InvokedFromNotDriverException
    static void overridedM(String s) {
        int x = intVal().eval();
    }

    // should go to this method
    static void overridedM(Object o) {
        int x = intVal().eval();
    }
}
