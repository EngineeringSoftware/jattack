package jattack.examples;

import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestDoubleValApis {

    private static double s1 = 0;

    @Entry
    public static void m() {
       if (relation(doubleVal(-5.0, -3.0),
                    doubleVal(3.0, 5.0), GT).eval()) {
           s1 += doubleVal().eval();
           throw new InvokedFromNotDriverException();
       } else {
           s1 += 1;
       }
    }
}
