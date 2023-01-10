package jattack.examples;

import jattack.annotation.Entry;

import java.util.ArrayList;
import java.util.List;

import static jattack.Boom.*;

public class SkTestOptStopEarlyCorrectnessForCollection {

    static List<Integer> l = new ArrayList<>();

    @Entry
    static void testCollection() {
        if (!l.isEmpty()) {
            // This hole is supposed to be filled if the entry method
            // is invoked more than once.
            int x = intVal().eval();
            return;
        }
        int y = intVal().eval();
        // modify l
        l.add(1);
    }
}
