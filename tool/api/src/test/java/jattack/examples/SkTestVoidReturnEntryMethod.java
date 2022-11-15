package jattack.examples;

import static jattack.Boom.*;
import jattack.annotation.Entry;

public class SkTestVoidReturnEntryMethod {

    @Entry
    public static void m() {
        int x = intVal().eval();
    }
}
