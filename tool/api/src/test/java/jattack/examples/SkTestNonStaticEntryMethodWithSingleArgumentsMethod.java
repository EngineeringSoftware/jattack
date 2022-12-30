package jattack.examples;

import static jattack.Boom.*;

import jattack.annotation.Arguments;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

public class SkTestNonStaticEntryMethodWithSingleArgumentsMethod {

    @Entry
    int m(int x, String y) {
        if (x != (int) args()[1] || !y.equals(args()[2])) {
            throw new InvokedFromNotDriverException();
        }
        return intVal().eval();
    }

    @Arguments
    static Object[] args() {
        return new Object[]{
                new SkTestNonStaticEntryMethodWithSingleArgumentsMethod(),
                1,
                "hello"
        };
    }
}
