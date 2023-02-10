package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestCastApi {

    @Entry
    static void m() {
        // Cast to int
        int a = arithmetic(cast(Integer.class, byteVal()), intVal()).eval();
        int b = arithmetic(cast(Integer.class, shortVal()), intVal()).eval();
        if (relation(cast(Integer.class, byteVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Integer.class, shortVal()), intVal()).eval()) {
            int x = intVal().eval();
        }

        // Cast to long
        long c = arithmetic(cast(Long.class, byteVal()), longVal()).eval();
        long d = arithmetic(cast(Long.class, shortVal()), longVal()).eval();
        long e = arithmetic(cast(Long.class, intVal()), longVal()).eval();
        if (relation(cast(Long.class, byteVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, shortVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, intVal()), longVal()).eval()) {
            int x = intVal().eval();
        }

        // Cast to float
        float f = arithmetic(cast(Float.class, byteVal()), floatVal()).eval();
        float g = arithmetic(cast(Float.class, shortVal()), floatVal()).eval();
        float h = arithmetic(cast(Float.class, intVal()), floatVal()).eval();
        float i = arithmetic(cast(Float.class, longVal()), floatVal()).eval();
        if (relation(cast(Float.class, byteVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, shortVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, intVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, longVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }

        // Cast to double
        double j = arithmetic(cast(Double.class, byteVal()), doubleVal()).eval();
        double k = arithmetic(cast(Double.class, shortVal()), doubleVal()).eval();
        double l = arithmetic(cast(Double.class, intVal()), doubleVal()).eval();
        double m = arithmetic(cast(Double.class, longVal()), doubleVal()).eval();
        double n = arithmetic(cast(Double.class, floatVal()), doubleVal()).eval();
        if (relation(cast(Double.class, byteVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, shortVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, intVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, longVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, floatVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
    }
}
