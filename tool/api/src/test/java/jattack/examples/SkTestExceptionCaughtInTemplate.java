package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestExceptionCaughtInTemplate {

    @Entry
    static void m() {
        try {
            int x = arithmetic(asInt(1), intVal(-1, 2), DIV).eval();
        } catch (ArithmeticException e) {
            // this exception is caught in the template, so it should
            // not be rethrown in an InvocationTargetException
        }
    }
}
