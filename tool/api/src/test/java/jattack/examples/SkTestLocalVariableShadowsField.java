package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestLocalVariableShadowsField {

    double d = 0;

    @Entry
    int m(String d) {
        int x = intVal().eval();
        return intId().eval();
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