package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestLocalVariableShadowsField {

    double d = 0;

    @Entry
    void m(String d) {
        // field "d" should be shadowed and not used here
        int x = intVal().eval();
        int y = intId().eval();
        m1();
    }

    void m1() {
        // field "d" should be found and used here
        int x = intVal().eval();
        double y = doubleId().eval();
    }

    @Argument(0)
    public static SkTestLocalVariableShadowsField receiver() {
        return new SkTestLocalVariableShadowsField();
    }

    @Argument(1)
    public static String arg1() {
        return null;
    }
}