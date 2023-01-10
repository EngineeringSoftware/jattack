package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestOptStopEarlyCorrectnessForImmutable {

    static String s = "hello";

    @Entry
    static void testInt() {
        if (!s.equals("hello")) {
            // This hole is supposed to be filled if the entry method
            // is invoked more than once.
            int x = intVal().eval();
            return;
        }
        int y = intVal().eval();
        // set i if i is 10
        s = "world";
    }
}
