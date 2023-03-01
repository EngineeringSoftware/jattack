package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestReceiverOfEntryMethodIsNull {

    @Entry
    void m(int x) {
    }

    @Argument(0)
    public static SkTestReceiverOfEntryMethodIsNull receiver() {
        return null;
    }

    @Argument(1)
    public static int arg1() {
        return intVal().eval();
    }
}
