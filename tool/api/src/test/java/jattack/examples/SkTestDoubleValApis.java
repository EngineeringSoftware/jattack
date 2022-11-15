package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestDoubleValApis {

    private static double s1 = 0;

    @Entry
    public static void m() {
       if (relation(doubleVal(-5.0, -1.0), doubleVal(1.0, 5.0), GT).eval()) {
           s1 += doubleVal().eval();
       } else {
           s1 += 1;
       }
    }
}
