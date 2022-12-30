package jattack.examples;

import static jattack.Boom.*;

import jattack.annotation.Argument;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

public class SkTestNonStaticEntryMethodWithSeparateArgumentMethods {

    @Entry
    int m(int x, String y) {
        if (x != arg1() || !y.equals(arg2())) {
            throw new InvokedFromNotDriverException();
        }
        return intVal().eval();
    }

    @Argument(0)
    static SkTestNonStaticEntryMethodWithSeparateArgumentMethods receiver() {
        return new SkTestNonStaticEntryMethodWithSeparateArgumentMethods();
    }

    @Argument(1)
    static int arg1() {
        return 1;
    }

    @Argument(2)
    static String arg2() {
        return "hello";
    }
}
