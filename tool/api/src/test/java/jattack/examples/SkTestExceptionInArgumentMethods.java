package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;

import static jattack.Boom.*;

// Expect only one generated program but neither jattack nor generated
// program should not crash when run as we already handled any
// exception from application.
public class SkTestExceptionInArgumentMethods {

    @Entry
    int m() {
        return intVal().eval();
    }

    @Argument(0)
    static SkTestExceptionInArgumentMethods makeReceiver() {
        if (asBool(true).eval()) {
            throw new RuntimeException("Some error of application.");
        }
        return new SkTestExceptionInArgumentMethods();
    }
}
