package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestHoleInArgument {

    @Entry
    public static int m(int i) {
        return makeInt();
    }

    @Argument(1)
    public static int arg1() {
        return makeInt();
    }

    static int makeInt() {
        return intVal().eval();
    }
}