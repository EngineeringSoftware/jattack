package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestCastApi {

    @Entry
    static void m() {
        // none can be promoted to byte, char, boolean
        // testCastToShort(); we currently do not support short in arithmetic
        testCastToInt();
        testCastToLong();
        testCastToFloat();
        testCastToDouble();
    }

    static void testCastToShort() {
        // Cast to short
        int a = arithmetic(cast(Short.class, byteVal()), shortVal()).eval();
        if (relation(cast(Short.class, byteVal()), shortVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToInt() {
        // Cast to int
        int a = arithmetic(cast(Integer.class, byteVal()), intVal()).eval();
        int b = arithmetic(cast(Integer.class, shortVal()), intVal()).eval();
        int c = arithmetic(cast(Integer.class, charVal()), intVal()).eval();
        if (relation(cast(Integer.class, byteVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Integer.class, shortVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Integer.class, charVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToLong() {
        // Cast to long
        long a = arithmetic(cast(Long.class, byteVal()), longVal()).eval();
        long b = arithmetic(cast(Long.class, shortVal()), longVal()).eval();
        long c = arithmetic(cast(Long.class, intVal()), longVal()).eval();
        long d = arithmetic(cast(Long.class, charVal()), longVal()).eval();
        if (relation(cast(Long.class, byteVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, shortVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, intVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, charVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToFloat() {
        // Cast to float
        float a = arithmetic(cast(Float.class, byteVal()), floatVal()).eval();
        float b = arithmetic(cast(Float.class, shortVal()), floatVal()).eval();
        float c = arithmetic(cast(Float.class, intVal()), floatVal()).eval();
        float d = arithmetic(cast(Float.class, longVal()), floatVal()).eval();
        float e = arithmetic(cast(Float.class, charVal()), floatVal()).eval();
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
        if (relation(cast(Float.class, charVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToDouble() {
        // Cast to double
        double a = arithmetic(cast(Double.class, byteVal()), doubleVal()).eval();
        double b = arithmetic(cast(Double.class, shortVal()), doubleVal()).eval();
        double c = arithmetic(cast(Double.class, intVal()), doubleVal()).eval();
        double d = arithmetic(cast(Double.class, longVal()), doubleVal()).eval();
        double e = arithmetic(cast(Double.class, floatVal()), doubleVal()).eval();
        double f = arithmetic(cast(Double.class, charVal()), doubleVal()).eval();
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
        if (relation(cast(Double.class, charVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
    }
}
