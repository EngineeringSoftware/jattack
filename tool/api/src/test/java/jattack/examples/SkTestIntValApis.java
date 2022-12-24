package jattack.examples;

import jattack.annotation.*;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestIntValApis {

    @Entry
    public static int m() {
        int i = intVal().eval();
        int j = intVal(0, 3).eval();
        if (relation(intVal(-5, -1), intVal(1, 5), GT).eval()) {
            i += doubleVal().eval();
            throw new InvokedFromNotDriverException();
        } else {
            j += 1;
        }
        return i + j;
    }
}
